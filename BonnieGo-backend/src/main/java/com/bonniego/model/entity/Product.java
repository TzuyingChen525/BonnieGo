package com.bonniego.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name="products")
public class Product {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Long product_id;
	
	@Column(name = "product_name", nullable = false)
	private String name;
	
	@Column(name = "product_description")
	private String description;

	@Column(name = "product_category")
	private String category;

	@Column(name = "product_brand")
	private String brand;
	
	@Column(name = "product_price", nullable = false)
	private Double price;

	@Column(name = "product_stock", columnDefinition = "int default 0")
	private Integer stock;

	@Column(name = "image_url")
	private String imageUrl;

}
