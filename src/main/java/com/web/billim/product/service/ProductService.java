package com.web.billim.product.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.web.billim.product.domain.ImageProduct;
import com.web.billim.product.domain.Product;
import com.web.billim.product.domain.ProductCategory;
import com.web.billim.product.dto.response.MyProductSalesResponse;
import com.web.billim.member.domain.Member;
import com.web.billim.member.repository.MemberRepository;
import com.web.billim.product.dto.response.ProductListResponse;
import com.web.billim.product.repository.ImageProductRepository;
import com.web.billim.product.repository.ProductCategoryRepository;
import com.web.billim.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.web.billim.product.dto.request.ProductRegisterRequest;
import com.web.billim.security.domain.User;
import com.web.billim.common.infra.ImageUploadService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ImageProductRepository imageProductRepository;
    private final ImageUploadService imageUploadService;
    private final ReviewService reviewService;

    public Product register(ProductRegisterRequest request) {
        Member registerMember = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));
        ProductCategory productCategory = productCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("해당 카테고리를 찾을 수 없습니다."));

        // 1. 이미지 저장
        List<ImageProduct> images = request.getImages().stream().map(image -> {
            try {
                String url = imageUploadService.upload(image, "product");
                return imageProductRepository.save(ImageProduct.of(url));
            } catch (IOException e) {
                throw new RuntimeException("파일 업로드중 에러가 발생했습니다.", e);
            }
        }).collect(Collectors.toList());

        // 2. Product 정보 데이터베이스에 저장 & 반환
        Product product = Product.generateNewProduct(request, productCategory, registerMember, images);
        return productRepository.save(product);
    }

    public List<ProductCategory> categoryList() {
        return productCategoryRepository.findAll();
    }


    public Page<ProductListResponse> findAllProduct(int page) {
        PageRequest paging = PageRequest.of(page, 12);
        return productRepository.findAllByOrderByCreatedAtDesc(paging).map(product -> {
            double starRating = reviewService.calculateStarRating(product.getProductId());
            return ProductListResponse.of(product, starRating);
        });
    }

//    public Page<Product> findAllProduct(int page) {
//        PageRequest paging = PageRequest.of(page, 12);
//        return productRepository.findAll(paging);
//    }


    @Transactional
    public Product retrieve(int productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("해당 ProductId(" + productId + ") 에 대한 상품정보가 없습니다."));
        return product;
    }


    public List<MyProductSalesResponse> myProduceSales(User user) {
        return productRepository.findByMember_memberId(user.getMemberId()).stream()
                .map(MyProductSalesResponse::of)
                .collect(Collectors.toList());
    }

    public Optional<Product> findProduct(int i) {
        return productRepository.findById(i);
    }


//    public ReservationDateResponse reservationDate(int productId) {
//        Optional<ProductOrder> productOrder = Optional.ofNullable(orderRepository.findByProductId(productId)
//                .orElseThrow(() ->
//                        new RuntimeException("해당 ProductId(" + productId + ") 에 대한 예약날짜가 없습니다.")));
//        return (ReservationDateResponse) productOrder.stream().map(ReservationDateResponse::of)
//                .collect(Collectors.toList());
//
//    }





}

