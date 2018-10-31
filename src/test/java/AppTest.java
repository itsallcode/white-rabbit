import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class AppTest {
	@Test
	void testAppHasAGreeting() {
		final App classUnderTest = new App();
		assertNotNull("app should have a greeting", classUnderTest.getGreeting());
	}
}
