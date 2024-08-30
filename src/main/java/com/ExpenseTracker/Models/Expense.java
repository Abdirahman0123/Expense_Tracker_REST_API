package com.ExpenseTracker.Models;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

//import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Document
//@Builder
@Data
@NoArgsConstructor
@Entity
@Table(name = "expenses")
public class Expense {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "item field required")
	private String item;

	@NotBlank(message = "shop field required")
	private String shop;

	//@NotEmpty(message = "price field required")
	@Min(value = 1L, message = "Minimum is £1")
	private int price;

	@NotBlank(message = "Category field required")
	private String category;

	@NotNull(message = "Date field required")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	private LocalDate date;

	@NotNull(message = "Time field required")
	// @JsonProperty
	private LocalTime time;

	//@JsonBackReference
	@ManyToOne()
	@JoinColumn(name = "user_id", nullable = false)
    private User user;

	
	public Expense( Long id,
			@JsonProperty(value = "item", required = true) String item,
			@JsonProperty(value = "shop", required = true) String shop,
			@JsonProperty(value = "price", required = true) int price,
			@JsonProperty(value = "category", required = true) String category,
			@JsonProperty(value = "date", required = true) LocalDate date,
			@JsonProperty(value = "time", required = true) LocalTime time

	) {
		this.id = id;
		this.item = item;
		this.shop = shop;
		this.price = price;
		this.category = category;
		this.date = date;
		this.time = time;
		
	}
}
