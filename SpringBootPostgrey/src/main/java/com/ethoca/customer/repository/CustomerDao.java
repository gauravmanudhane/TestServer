package com.ethoca.customer.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ethoca.customer.model.Customer;

@Repository
public class CustomerDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Page<Customer> findAllCustomersJDBC(Pageable page) {

		Order order = !page.getSort().isEmpty() ? page.getSort().toList().get(0) : Order.by("ID");

		List<Customer> customers = jdbcTemplate.query("SELECT * FROM CUSTOMER ORDER BY " + order.getProperty() + " "
				+ order.getDirection().name() + " LIMIT " + page.getPageSize() + " OFFSET " + page.getOffset(),
				(rs, rowNum) -> {
					Customer customer = new Customer();
					customer.setId(rs.getInt("ID"));
					customer.setName(rs.getString("NAME"));
					customer.setSector(rs.getString("SECTOR"));
					customer.setLocation(rs.getString("LOC"));
					return customer;
				});
		return new PageImpl<Customer>(customers, page, count());
	}

	public int count() {
		return jdbcTemplate.queryForObject("SELECT count(*) FROM CUSTOMER", Integer.class);
	}
}
