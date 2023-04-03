package com.web.billim.controller;

import com.web.billim.domain.Member;
import com.web.billim.domain.Product;
import com.web.billim.domain.ProductCategory;
import com.web.billim.dto.request.ProductRegisterRequest;
import com.web.billim.service.MemberService;
import com.web.billim.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    private final MemberService memberService;

    @GetMapping("/product/total")
    public String productTotal(Model model) {
        List<Product> productList = productService.findAllProduct();
        model.addAttribute("productList", productList);
        return "pages/product/productList";
    }

    @GetMapping("/product/detail/{productId}")
    public String productDetail(@PathVariable("productId") int productId, Model model) {
        Product product = productService.retrieve(productId);
        product.getImages().forEach(imageProduct -> {
            System.out.println(imageProduct.getUrl());
        });
        model.addAttribute("product", product);
        return "pages/product/productDetail";
    }

    @GetMapping("/myPage/sales")
    public String myPageSalesManagement() {
        return "pages/myPage/mySalesList";
    }

    @GetMapping("/product/enroll")
    public String productEnroll(Model model) {
        List<ProductCategory> categoryList = productService.categoryList();
        model.addAttribute("categoryList", categoryList);
        return "pages/product/productEnroll";
    }

    @PostMapping(value = "/product/enroll", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<Product> registerProduct(@ModelAttribute @Valid ProductRegisterRequest req) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        int memberId = (Integer) authentication.getPrincipal();
//        Member testMember = Member.builder().memberId(1).build();
        Member testMember = Member.builder().memberId(memberId).build();
        req.setRegisterMember(testMember);
        return ResponseEntity.ok(productService.register(req));
    }

//    @PostMapping("/product/enroll")
//    public String registerProduct(@Valid ProductRegisterRequest productRegisterRequest) {
//        Member testMember = Member.builder().memberId(1).build();
//        productRegisterRequest.setRegisterMember(testMember);
//        productService.register(productRegisterRequest);
//        return "pages/myPage/mySalesList";
//    }
}

