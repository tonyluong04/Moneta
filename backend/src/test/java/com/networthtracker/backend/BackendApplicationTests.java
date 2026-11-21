package com.networthtracker.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "moneta.fx.enabled=false") // no network calls from tests
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
