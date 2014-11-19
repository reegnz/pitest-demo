package reegnz.pitest_demo;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ThresholdValidatorTest {

	private ThresholdValidator validator;

	@Before
	public void setUp() {
		validator = new ThresholdValidator();
	}

	@Test
	public void should_return_true_when_over_threshold() {
		boolean actual = validator.isValid(6);
		assertThat(actual, is(true));
	}

	@Test
	public void should_return_false_when_under_threshold() {
		boolean actual = validator.isValid(4);
		assertThat(actual, is(false));
	}

	@Ignore
	@Test
	public void should_return_true_when_on_threshold() {
		boolean actual = validator.isValid(5);
		assertThat(actual, is(true));
	}
}
