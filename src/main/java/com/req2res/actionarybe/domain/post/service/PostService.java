package com.req2res.actionarybe.domain.post.service;

import com.req2res.actionarybe.domain.image.service.ImageService;
import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.domain.post.dto.*;
import com.req2res.actionarybe.domain.post.entity.Post;
import com.req2res.actionarybe.domain.post.entity.PostImage;
import com.req2res.actionarybe.domain.post.repository.PostImageRepository;
import com.req2res.actionarybe.domain.post.repository.PostRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final MemberRepository memberRepository;
    private final ImageService imageService;

    // 게시글 생성
    @Transactional
    public CreatePostResponseDTO createPost(CreatePostRequestDTO request, Long memberId, List<MultipartFile> images) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 게시글 종류 타입변환
        Post.Type type;
        try {
            type = Post.Type.valueOf(request.getType());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_POST_TYPE);
        }

        Post post = new Post(
                member,
                type,
                request.getTitle(),
                request.getContent().getText()
        );

        // 이미지가 있을 때만 처리 (S3 업로드)
        if (images != null && !images.isEmpty()) {
            int order = 0;
            for (MultipartFile imageFile : images) {

                // image없으면 image 저장없음
                if (imageFile.isEmpty()) continue;

                String imageUrl = imageService.saveImage(imageFile);
                post.addImage(new PostImage(post, imageUrl, order++));
            }
        }

        postRepository.save(post);

        return new CreatePostResponseDTO(
                post.getId(),
                post.getTitle(),
                member.getNickname(),
                post.getCreatedAt()
        );
    }

    // 게시글 조회 (post_id에 따른)
    public GetPostResponseDTO getPost(Long post_id) {
        Post post=postRepository.findById(post_id)
                .orElseThrow(()->new CustomException(ErrorCode.POST_NOT_FOUND));

        PostInfoDTO postInfo = new PostInfoDTO(
                post.getId(),
                post.getType().toString(),
                post.getTitle(),
                post.getText(),
                post.getCommentsCount(),
                post.getCreatedAt()
        );

        PostAuthorDTO postAuthor = new PostAuthorDTO(
                post.getMember().getId(),
                post.getMember().getNickname(),
                post.getMember().getProfileImageUrl(),
                post.getMember().getBadge().getId()
        );

        // 서버 꺼졌다가 켜져도, .getImages()하면 SQL 쿼리 날림 (@OneToMany(fetch = FetchType.LAZY이기에)
        List<String> images=post.getImages().stream()
                .map(postImage->postImage.getImageUrl()).toList();

        return new GetPostResponseDTO(
                postInfo,
                images,
                postAuthor
        );
    }

    // 삭제할 사진이 s3에 존재 유무 확인
    public boolean isDelImageInS3(String delImageUrl){
        Optional<PostImage> opt = Optional.ofNullable(postImageRepository.findByImageUrl(delImageUrl));

        if(opt.isPresent()) return true;
        else return false;
    }

    // 이미지 추가
    @Transactional
    public void addPostImages(List<MultipartFile> addImages, Post post) {
        int order = post.getImages().stream()
                .map(PostImage::getImageOrder) // PostImage 타입에서 imageOrder 필드만 가져온다.
                .max(Integer::compare) // order들 중 제일 큰 수를 고른다.
                .orElse(0); // 마지막으로 저장한 postImages의 order값
        for (MultipartFile imageFile : addImages) {

            // image없으면 image 저장없음
            if (imageFile.isEmpty()) continue;

            String imageUrl = imageService.saveImage(imageFile);
            post.addImage(new PostImage(post, imageUrl, ++order));
        }
    }

    // 이미지 삭제
    @Transactional
    public void delPostImages(String[] delImages, Post post) {
        // 이미지 삭제
        for(String imageUrl : delImages){
            if(isDelImageInS3(imageUrl)){
                imageService.deleteImage(imageUrl);
                post.removeImage(imageUrl);
            }else{
                throw new CustomException(ErrorCode.DEL_IMAGE_NOT_IN_S3);
            }
        }
    }

    // 게시글 수정
    @Transactional
    public UpdatePostResponseDTO updatePost(Long post_id, UpdateImageRequestDTO imagesDTO, UpdatePostRequestDTO postDTO) {
        Post post=postRepository.findById(post_id)
                .orElseThrow(()->new CustomException(ErrorCode.POST_NOT_FOUND));
        System.out.println("@^%#"+imagesDTO+"@&%#%@&");

        if(imagesDTO == null && post == null){
            throw new CustomException(ErrorCode.EMPTY_UPDATE_REQUEST);
        }

        if(imagesDTO != null){
            if(imagesDTO.getDelImages()!=null) delPostImages(imagesDTO.getDelImages(), post);
            if(imagesDTO.getAddImages()!=null) addPostImages(imagesDTO.getAddImages(), post);
        }
        if(postDTO != null){
            if(postDTO.getType()!=null)
                post.setType(Post.Type.valueOf(postDTO.getType())); // request의 type는 String이라, Post의 Type 자료형인 Post.Type(Enum) 타입 변환 필요
            if(postDTO.getTitle()!=null)
                post.setTitle(postDTO.getTitle());
            if(postDTO.getText()!=null)
                post.setText(postDTO.getText());
        }

        return new UpdatePostResponseDTO(
                post.getId(),
                post.getTitle()
        );
    }

    // 게시글 삭제
    @Transactional
    public DeletePostResponseDTO deletePost(Long post_id) {
        postRepository.deleteById(post_id);
        return new DeletePostResponseDTO(post_id);
    }

    // 최신 / 인기 게시글 공용
    public SortedResponseDTO getSortedPosts(Post.Type type, Pageable pageable) {

        Page<Post> page = (type == null)
                ? postRepository.findAll(pageable)
                : postRepository.findByType(type, pageable);

        List<PostSummaryDTO> posts = page.getContent().stream()
                .map(post -> new PostSummaryDTO(
                        post.getId(),
                        post.getType().name(),
                        post.getTitle(),
                        post.getMember().getNickname(),
                        post.getCommentsCount(),
                        post.getCreatedAt()
                ))
                .toList();

        PageInfoDTO pageInfo = new PageInfoDTO(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return new SortedResponseDTO(posts, pageInfo);
    }
}