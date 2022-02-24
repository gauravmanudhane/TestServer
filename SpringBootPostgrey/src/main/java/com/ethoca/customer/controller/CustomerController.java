package com.ethoca.customer.controller;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.ethoca.customer.model.Customer;
import com.ethoca.customer.repository.CustomerDao;
import com.ethoca.customer.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/customer-api")
public class CustomerController {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CustomerDao customerDao;

	@PostMapping("/insertCustomers/{start}/{end}")
	public ResponseEntity<String> insertCustomers(@PathVariable("start") int start, @PathVariable("end") int end) {
		System.out.println("insertCustomers()");
		try {
			List<Customer> customerList = new ArrayList<>();
			for (int i = start; i <= end; i++) {
				customerList.add(new Customer(i, "Echoca#" + i, "BF-US#" + i, "US#" + i));
				if (i % 10000 == 0) {
					customerRepository.saveAll(customerList);
					customerList.clear();
				}
			}
			return new ResponseEntity<>("SUCCESS", HttpStatus.CREATED);
		} catch (Exception e) {
			System.out.println("Exception: " + e);
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/deleteAllCustomers")
	public ResponseEntity<HttpStatus> deleteAllCustomers() {
		System.out.println("deleteAllCustomers()");
		try {
			customerRepository.deleteAll();
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			System.out.println("Exception: " + e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getAllCustomers")
	public ResponseEntity<List<Customer>> getAllCustomers() {
		System.out.println("getAllCustomers()");
		try {
			List<Customer> customers = new ArrayList<>();

			customerRepository.findAll(Sort.by("id").ascending()).forEach(customers::add);

			if (customers.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(customers, HttpStatus.OK);
		} catch (Exception e) {
			System.out.println("Exception: " + e);
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//	@GetMapping("/getAllCustomers/streaming-response")
//	public ResponseEntity<StreamingResponseBody> getAllCustomersStreamingResponse() {
//		System.out.println("getAllCustomersStreamingResponse()");
//		StreamingResponseBody customersStreamingResponseBody = response -> {
//			int maxRecords = 200;
//			for (int i = 0; i < maxRecords; i++) {
//				ObjectMapper objectMapper = new ObjectMapper();
//
//				List<Customer> customers = new ArrayList<Customer>();
//				Pageable pagingSort = PageRequest.of(i, 10000, Sort.by("id").ascending());
//				customerRepository.findAll(pagingSort).forEach(customers::add);
//
//				System.out.println("Count: " + i);
//				System.out.println("getAllCustomersStreamingResponse(): " + customers.size());
//
//				response.write(objectMapper.writeValueAsString(customers).getBytes());
//				response.flush();
//			}
//		};
//		return ResponseEntity.ok().contentType(MediaType.APPLICATION_STREAM_JSON).body(customersStreamingResponseBody);
//	}

	@GetMapping("/getAllCustomers/streaming-response")
	public ResponseEntity<StreamingResponseBody> getAllCustomersStreamingResponse() {
		System.out.println("getAllCustomersStreamingResponse()");
		StreamingResponseBody customersStreamingResponseBody = response -> {
			Writer writer = new BufferedWriter(new OutputStreamWriter(response));
			writer.write("ID, NAME, SECTOR, LOC" + "\n");
			int maxRecords = 200;
			for (int i = 0; i < maxRecords; i++) {
				List<Customer> customers = new ArrayList<Customer>();
				Pageable pagingSort = PageRequest.of(i, 10000, Sort.by("id").ascending());
				customerRepository.findAll(pagingSort).forEach(customers::add);

				System.out.println("Count: " + i);
				System.out.println("getAllCustomersStreamingResponse(): " + customers.size());

				for (Customer customer : customers) {
					writer.write(customer.getId() + "," + customer.getName() + "," + customer.getSector() + ","
							+ customer.getLocation() + "\n");
					writer.flush();
				}
			}
		};
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=customer-data.csv")
				.contentType(MediaType.TEXT_PLAIN).body(customersStreamingResponseBody);
	}

	@GetMapping("/getAllCustomersJdbc/streaming-response")
	public ResponseEntity<StreamingResponseBody> getAllCustomersJdbcStreamingResponse() {
		System.out.println("getAllCustomersStreamingResponse()");
		StreamingResponseBody customersStreamingResponseBody = response -> {
			int maxRecords = 200;
			for (int i = 0; i < maxRecords; i++) {
				ObjectMapper objectMapper = new ObjectMapper();

				Pageable pageable = PageRequest.of(i, 10000, Sort.by("id").ascending());
				Page<Customer> customers = customerDao.findAllCustomersJDBC(pageable);

				System.out.println("Count: " + i);
				System.out.println("getAllCustomersStreamingResponse(): " + customers.getSize());

				response.write(objectMapper.writeValueAsString(customers.getContent()).getBytes());
				response.flush();
			}
		};
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_STREAM_JSON).body(customersStreamingResponseBody);
	}

}
