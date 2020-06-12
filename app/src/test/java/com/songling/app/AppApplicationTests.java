package com.songling.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient(autoRegister = true)

@SpringBootTest
class AppApplicationTests {

	@Test
	void contextLoads() {
	}

}
