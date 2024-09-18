package com.ExpenseTracker.Controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ExpenseTracker.Models.Expense;
import com.ExpenseTracker.Models.User;
import com.ExpenseTracker.Repositories.ExpenseRepository;
import com.ExpenseTracker.Services.ExpenseService;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/v1/api/expenses")
public class ExpenseController {

	@Autowired
	public ExpenseService expenseService;

	@Autowired
	public ExpenseRepository expenseRepo;

	/*
	 * @Autowired Jackson2ObjectMapperBuilder mapperBuilder;
	 */

	@Autowired
	private ObjectMapper mapper;
	/*
	 * @GetMapping() public ResponseEntity<List<Expense>> getAllExpenses() {
	 * List<Expense> expenses = expenseService.getExpenses();
	 * 
	 * return ResponseEntity.ok(expenses); }
	 */

	@GetMapping()
	public ResponseEntity<Map<String, Object>> getAllExpenses(
			@RequestParam(required = false) String category,
			@RequestParam(defaultValue = "0") int page, 
			@RequestParam(defaultValue = "3") int size,
			@RequestParam(defaultValue = "id,desc") String[] sort) {

		try {
			List<Order> orders = new ArrayList<>();

			// if the sort array contains ",", we will sort the expenses
			// by 2 fields. Else, we will sort the result by one field
			if (sort[0].contains(",")) {
				for (String sortOrder : sort) {
					String[] _sort = sortOrder.split(",");
					orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
				}
			}

			else {
				orders.add(new Order(getSortDirection(sort[1]), sort[0]));
			}

			List<Expense> expenses = new ArrayList<Expense>();

			// create paging based on page, size and SortBy;
			Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

			Page<Expense> pageExpenses;

			// Page<Expense> pageTuts;

			if (category == null) {
				//pageExpenses = expenseService.getExpenses(pagingSort);
				pageExpenses = expenseService.getExpenses(pagingSort);
			} else {
				pageExpenses = expenseService.findByCategory(category, pagingSort);
				//pageExpenses = expenseService.getExpenses(pagingSort);
			}

			expenses = pageExpenses.getContent();

			if (expenses.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}

			Map<String, Object> response = new HashMap<>();
			response.put("expennses", expenses);
			response.put("currentPage", pageExpenses.getNumber());
			response.put("totalItems", pageExpenses.getTotalElements());
			response.put("totalPages", pageExpenses.getTotalPages());

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			//return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
			//return new ResponseEntity<>(null, e.getStackTrace());
			//throw new ResponseStatusException(HttpStatus.OK, e.getStackTrace());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occured", e);
			//throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Actor Not Found", e);
		}

	}
	
	
	// Retrieve one expense
	@GetMapping("/{id}")
	public ResponseEntity<Optional<Expense>> getExpense(@PathVariable Long id) {

		/*if (id == null || id == "") {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid id");
		}*/
	
		Optional<Expense> expense = expenseService.getExpense(id);
		if (expense.isPresent()) {
			return ResponseEntity.ok(expense);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Record does not exists");
		}

	}
	
	
	// add an expense
	@PostMapping()
	public ResponseEntity<Expense> addExpense(@RequestBody @Valid Expense expense) {
		
		/*User user = new User();*/
		// trucate milliseconds from the time
		LocalTime time = expense.getTime();
		LocalTime truncatedTime = time.truncatedTo(ChronoUnit.SECONDS);
		expense.setTime(truncatedTime);
		
		// get current user
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();
        
        expense.setUser(currentUser);
        
		expenseService.saveExpense(expense);
		return new ResponseEntity<Expense>(expense, HttpStatus.CREATED);

	}

	// delete an expense
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteExpense(@PathVariable Long id) {

		/*if (id == null || id.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid id");
		}*/

		Optional<Expense> expense = expenseService.getExpense(id);
		if (expense.isPresent()) {
			expenseService.deleteExpense(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Record does not exists");
		}
			
			/*expenseService.deleteExpense(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);*/
		
	}
	


	/*
	 * three objects here newExpenseData: new expense data that the user just
	 * entered oldExpenseData: old data which has been retrieve from the database
	 * expense: a new objects which is instantiated with oldExpensedat and then
	 * updated with the newExpenseData. expense is then saved to the database
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @RequestBody @Valid Expense newExpenseData) {

		Optional<Expense> oldExpenseData = expenseService.getExpense(id);
		Expense expense = oldExpenseData.get();
		if (oldExpenseData.isPresent()) {
			expense.setItem(newExpenseData.getItem());
			expense.setShop(newExpenseData.getShop());
			expense.setPrice(newExpenseData.getPrice());
			expense.setCategory(newExpenseData.getCategory());
			expense.setDate(newExpenseData.getDate());
			expense.setTime(newExpenseData.getTime());
			return new ResponseEntity<Expense>(expense, HttpStatus.OK);
		}

		else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}


	// partialUpdate
	/*
	 * find the expense using id and then pass the returned record to
	 * applyPatchToExpense. Then, save the expense to the database and return it
	 * using ResponseEntity with 200 status code
	 */
	@PatchMapping("/{id}")
	public ResponseEntity<Expense> patchUpdate(@PathVariable Long id, @RequestBody @Valid 
			JsonPatch patch) throws JsonProcessingException, JsonPatchException
			/*throws JsonProcessingException, JsonPatchException, JsonMappingException*/  {
		Expense expensePatched = new Expense();
		
		//patch.
		try {
			
			Optional<Expense> expense = expenseRepo.findById(id);
			expensePatched = applyPatchToExpense(patch, expense);
			expenseService.saveExpense(expensePatched);

			return new ResponseEntity<Expense>(expensePatched, HttpStatus.OK);

		} catch (JsonMappingException ex) {
			//throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Record Not Found", ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		

	}

	// This method applies json patch
	/*
	 * expense instance is converted to JasonNode using convertValue, which is then
	 * passed to JsonPatch.apply method to apply the patch.
	 */
	
	private Expense applyPatchToExpense(JsonPatch patch, Optional<Expense> expense)
			throws JsonPatchException, JsonProcessingException {
		// ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		JsonNode patched = patch.apply(mapper.convertValue(expense, JsonNode.class));
		return mapper.treeToValue(patched, Expense.class);
	}
	
	/*
	 * this method sorts the result based on the direction if the direction is asc,
	 * the result is sorted from smallest to largest if the direction is desc, the
	 * result is sorted from largest to smallest
	 * 
	 */
	private Sort.Direction getSortDirection(String direction) {
		if (direction.equals("asc")) {
			return Sort.Direction.ASC;
		} else if (direction.equals("desc")) {
			return Sort.Direction.DESC;
		}

		return Sort.Direction.ASC;
	}
	
	// filter expense by one week and retrieve it
	@GetMapping("/week")
	public ResponseEntity<?> filterByOneWeek(
			@RequestParam(required = false) LocalDate today,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size,
			@RequestParam(defaultValue = "id,desc") String[] sort) {

		try {
			List<Order> orders = new ArrayList<>();

			// if the sort array contains ",", we will sort the expenses
			// by 2 fields. Else, we will sort the result by one field
			if (sort[0].contains(",")) {
				for (String sortOrder : sort) {
					String[] _sort = sortOrder.split(",");
					orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
				}
			}

			else {
				// sort=[field, direction]
				orders.add(new Order(getSortDirection(sort[1]), sort[0]));
			}

			List<Expense> expenses = new ArrayList<Expense>();

			// create paging based on page, size and sort using PageRequest;
			Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

			Page<Expense> pageExpenses;

			if (today == null) {
				pageExpenses = expenseRepo.findAll(pagingSort);
			} else {

				LocalDate lastWeek = today.minusWeeks(1);

				// System.out.print("last Month not working" + lastMonth);
				pageExpenses = expenseService.filterByDates(lastWeek, today, pagingSort);
			}

			// retrieve the List of items in the page
			expenses = pageExpenses.getContent();

			expenses.forEach(System.out::println);
			// if expenses list is empty, return No content
			if (expenses.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}

			Map<String, Object> response = new HashMap<>();
			response.put("expennses", expenses);
			// getNumber means current page
			response.put("currentPage", pageExpenses.getNumber());
			// getTotalElements means get total elements which are stored in the database
			response.put("totalItems", pageExpenses.getTotalElements());
			// getTotalPages() means get total number of pages
			response.put("totalPages", pageExpenses.getTotalPages());

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public List<Expense> filter(int size, int page, LocalDate lastWeek, LocalDate today, String[] sort) {

		Map<String, Object> response = new HashMap<>();

		// try {
		List<Order> orders = new ArrayList<>();

		// if the sort array contains ",", we will sort the expenses
		// by 2 fields. Else, we will sort the result by one field
		if (sort[0].contains(",")) {
			for (String sortOrder : sort) {
				String[] _sort = sortOrder.split(",");
				orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
			}
		}

		else {
			orders.add(new Order(getSortDirection(sort[1]), sort[0]));
		}

		List<Expense> expenses = new ArrayList<Expense>();

		// create paging based on page, size and sort using PageRequest;
		Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

		Page<Expense> pageExpenses;

		if (today == null) {
			pageExpenses = expenseRepo.findAll(pagingSort);
		} else {

			// LocalDate lastWeek = today.minusWeeks(1);

			// System.out.print("last Month not working" + lastMonth);
			pageExpenses = expenseService.filterByDates(lastWeek, today, pagingSort);
		}

		// retrieve the List of items in the page
		expenses = pageExpenses.getContent();

		// if expenses list is empty, return No content
		/*
		 * if (expenses.isEmpty()) { return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		 * }
		 */

		return expenses;

	}

	@GetMapping("/month")
	public ResponseEntity<Map<String, Object>> filterByOneMonth(@RequestParam(required = false) LocalDate today,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size,
			@RequestParam(defaultValue = "id,desc") String[] sort) {

		try {
			List<Order> orders = new ArrayList<>();

			// if the sort array contains ",", we will sort the expenses
			// by 2 fields. Else, we will sort the result by one field
			if (sort[0].contains(",")) {
				for (String sortOrder : sort) {
					String[] _sort = sortOrder.split(",");
					orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
				}
			}

			else {
				// sort=[field, direction]
				orders.add(new Order(getSortDirection(sort[1]), sort[0]));
			}

			List<Expense> expenses = new ArrayList<Expense>();

			// create paging based on page, size and sort using PageRequest;
			Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

			Page<Expense> pageExpenses;

			if (today == null) {
				pageExpenses = expenseRepo.findAll(pagingSort);
			} else {

				LocalDate lastMonth = today.minusMonths(1);

				System.out.print("last Month not working" + lastMonth);
				pageExpenses = expenseService.filterByDates(lastMonth, today, pagingSort);
			}

			// retrieve the List of items in the page
			expenses = pageExpenses.getContent();

			// if expenses list is empty, return No content
			if (expenses.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}

			Map<String, Object> response = new HashMap<>();
			response.put("expennses", expenses);
			// getNumber means current page
			response.put("currentPage", pageExpenses.getNumber());
			// getTotalElements means get total elements which are stored in the database
			response.put("totalItems", pageExpenses.getTotalElements());
			// getTotalPages() means get total number of pages
			response.put("totalPages", pageExpenses.getTotalPages());

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// retrieve expenses from last three months
	@GetMapping("/threeMonths")
	public ResponseEntity<Map<String, Object>> filterByThreeMonths(@RequestParam(required = false) LocalDate today,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size,
			@RequestParam(defaultValue = "id,desc") String[] sort) {

		try {
			List<Order> orders = new ArrayList<>();

			// if the sort array contains ",", we will sort the expenses
			// by 2 fields. Else, we will sort the result by one field
			if (sort[0].contains(",")) {
				for (String sortOrder : sort) {
					String[] _sort = sortOrder.split(",");
					orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
				}
			}

			else {
				// sort=[field, direction]
				orders.add(new Order(getSortDirection(sort[1]), sort[0]));
			}

			List<Expense> expenses = new ArrayList<Expense>();

			// create paging based on page, size and sort using PageRequest;
			Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

			Page<Expense> pageExpenses;

			if (today == null) {
				pageExpenses = expenseRepo.findAll(pagingSort);
			} else {

				LocalDate lastMonth = today.minusMonths(3);

				System.out.print("last Month not working" + lastMonth);
				pageExpenses = expenseService.filterByDates(lastMonth, today, pagingSort);
			}

			// retrieve the List of items in the page
			expenses = pageExpenses.getContent();

			// if expenses list is empty, return No content
			if (expenses.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}

			Map<String, Object> response = new HashMap<>();
			response.put("expennses", expenses);
			// getNumber means current page
			response.put("currentPage", pageExpenses.getNumber());
			// getTotalElements means get total elements which are stored in the database
			response.put("totalItems", pageExpenses.getTotalElements());
			// getTotalPages() means get total number of pages
			response.put("totalPages", pageExpenses.getTotalPages());

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	
	// filter expenses by custom date and return expenses between these dates
		@GetMapping("/customDate")
		public ResponseEntity<Map<String, Object>> filterByCustomDate(
				@RequestParam(required = false) LocalDate startDate,
				@RequestParam(required = false) LocalDate endDate, 
				@RequestParam(defaultValue = "0") int page,
				@RequestParam(defaultValue = "3") int size, 
				@RequestParam(defaultValue = "id,desc") String[] sort) {

			try {
				List<Order> orders = new ArrayList<>();

				// if the sort array contains ",", we will sort the expenses
				// by 2 fields. Else, we will sort the result by one field
				if (sort[0].contains(",")) {
					for (String sortOrder : sort) {
						String[] _sort = sortOrder.split(",");
						orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
					}
				}

				else {
					orders.add(new Order(getSortDirection(sort[1]), sort[0]));
				}

				List<Expense> expenses = new ArrayList<Expense>();

				// create paging based on page, size and sort using PageRequest;
				Pageable pagingSort = PageRequest.of(page, size, Sort.by(orders));

				Page<Expense> pageExpenses;

				if (startDate == null && endDate == null) {
					pageExpenses = expenseRepo.findAll(pagingSort);
				} else {
					pageExpenses = expenseService.filterByDates(startDate, endDate, pagingSort);
				}

				// retrieve the List of items in the page
				expenses = pageExpenses.getContent();

				// if expenses list is empty, return No content
				if (expenses.isEmpty()) {
					return new ResponseEntity<>(HttpStatus.NO_CONTENT);
				}

				Map<String, Object> response = new HashMap<>();
				response.put("expennses", expenses);
				// getNumber means current page
				response.put("currentPage", pageExpenses.getNumber());
				// getTotalElements means get total elements which are stored in the database
				response.put("totalItems", pageExpenses.getTotalElements());
				// getTotalPages() means get total number of pages
				response.put("totalPages", pageExpenses.getTotalPages());

				return new ResponseEntity<>(response, HttpStatus.OK);
			} catch (Exception e) {
				return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
			}

		}
	
		
}
