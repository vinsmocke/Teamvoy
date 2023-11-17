package com.teamvoy.task.controller;

import com.teamvoy.task.dto.goodsDto.GoodsRequest;
import com.teamvoy.task.dto.goodsDto.GoodsTransformer;
import com.teamvoy.task.model.Product;
import com.teamvoy.task.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/goods")
public class ProductController {
    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @PreAuthorize("@check.isManager() or @check.isClient()")
    public List<Product> getAll() {
        return productService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@check.isManager() or @check.isClient()")
    public Product getById(@PathVariable("id") long id) {
        return productService.readById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@check.isManager()")
    public Product create(@RequestBody @Valid GoodsRequest goodsDto) {
        return productService.create(GoodsTransformer.convertToEntity(goodsDto));
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("@check.isManager()")
    public Product update(@RequestParam long id, @RequestBody @Valid GoodsRequest goodsDto) {
        Product product = productService.readById(id);
        return productService.update(GoodsTransformer.convertToEntityForUpdate(goodsDto, product));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("@check.isManager()")
    public ResponseEntity<String> delete(@PathVariable("id") long id) {
        productService.delete(id);
        return ResponseEntity.accepted().body("Goods with id " + id + " has been removed");
    }
}
