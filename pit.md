class: center, middle, inverse

# Killing mutants in the PITs
## a.k.a. mutation testing
---

# Agenda

1. A few words on code coverage
2. Mutation testing
3. PIT
4. Demo Time!
5. Conclusion

---
class: center, middle, inverse

##Code coverage is close to useless
It just tells you what is definitely **NOT** tested

???

Bloom filter - test whether an element is a member of a set or not.

False positives are possible, but false negatives are not.
* positive: row is possibly tested.
* negative: row is definitely not tested.

Coverage analysis is the most common way of ensuring that testing really does test the software.s
---
class: center, middle, inverse

##Code Coverage gives a false sense of security
"If there is line/branch coverage, it surely is tested!" - Delusional Programmer

???

Coverage only tests syntax, not semantics.

It doesn't guarantee that you have made valuable assertions.

100% code coverage does not mean that your code is bug free. It doesn’t even mean that your code is being properly tested.

You can produce 100% code coverage without making any assertions.
---
class: center, middle, inverse
##Example on how code coverage may fail you
---
class: middle
##Code to be tested
```java
public class ThresholdValidator {

	public boolean isValid(int number) {
		return !(number < 5);
	}
}
```

???

This is just a simple validator. It validates if a number is not below the threshold (here that is 5).
---
class: middle
##Tests producing 100% coverage
```java
public class ThresholdValidatorTest {

	private ThresholdValidator validator;

	@Before
	public void setUp() {
		validator = new ThresholdValidator();
	}

	@Test
	public void test1() {
		validator.isValid(6);
	}

	@Test
	public void test2() {
		validator.isValid(4);
	}
}
```

???

It is clear that these tests are useless. They do not have any assertions present. But they will cause 100% line coverage, and even 100% branch coverage.
---
class: middle
##Tests actually testing something
```java
public class ThresholdValidatorTest {

	private ThresholdValidator validator;

	@Before
	public void setUp() {
		validator = new ThresholdValidator();
	}

	@Test
	public void should_return_true_when_over_threshold() {
		boolean actual = validator.isValid(6);
*		assertThat(actual, is(true));
	}

	@Test
	public void should_return_false_when_under_threshold() {
		boolean actual = validator.isValid(4);
*		assertThat(actual, is(false));
	}
}
```

???
Now these tests we like. They have assertions, and now our 100% coverage does mean that the covered lines are tested. Or does it?

Let's mess around with our code (inject some bugs) and see if the tests will find the introduced bug.
---
class: middle

#Mutation 1
Original
```java
public class ThresholdValidator {

	public boolean isValid(int number) {
		return !(number < 5);
	}
}
```
Mutant
```java
public class ThresholdValidator {
	
	public boolean isValid(int number) {
*		return = !(!(number < 5));
	}
}
```

???

Here we negate the return value, so if true would be returned, we get a false return value instead.

Both of our tests will fail. The honour of 100% coverage is still safe. 
---
class: middle

#Mutation 2
Original
```java
public class ThresholdValidator {

	public boolean isValid(int number) {
		return !(number < 5);
	}
}
```
Mutant
```java
public class ThresholdValidator {

	public boolean isValid(int number) {
*		return !(number >= 5);
	}
}
```

???

This mutation negates the conditional in our expression. This is equivalent in result to the previous one, but it is a different bug.

Our 100% coverage is still safe.
---
class: middle

#Mutation 3
Original
```java
public class ThresholdValidator {

	public boolean isValid(int number) {
		return !(number < 5);
	}
}
```
Mutant
```java
public class ThresholdValidator {
	
	public boolean isValid(int number) {
*		return !(number <= 5);
	}
}
```

???
This bug is introduced by changing the conditional boundary just a little.

Before the change our validator accepted 5 as a valid number. After the change it rejects it.

Do our tests notice this change and fail? No!

The honour of the 100% code coverage is destroyed by this bug. The tests failed to notice it, even though we have 100% line and branch coverage.
---
class: middle

#Additional test needed
```java
public class ThresholdValidatorTest {

	...

*	@Test
*	public void should_return_true_when_on_threshold() {
*		boolean actual = validator.isValid(5);
*		assertThat(actual, is(true));
*	}
}
```
???
The missing piece in our test suite is a test that tests the boundary condition too.

If this test is present, it will notice if someone messed around with the boundary condition.
---
class: center, middle, inverse
##Quis custodiet ipsos custodes?
##Who will guard the guards themselves?
Who watches the watchmen?

Who tests the tests?

???

Injecting bugs into the System Under Test (SUT) and checking if any test fails with the bug.
This is called mutation testing.
---
class: middle
##Mutation testing

* Create an alternate SUT - this alternate System will be called a **Mutant**

* Run the tests against the mutant
  * If at least one test fails - the Mutant is **Killed**
  * If no tests fail, then our test suite is not complete - the Mutant has **Survived**

---
class:middle
##Injected bugs

competent programmer hypothesis - most faults are due to small syntactic errors
coupling effect - simple faults cascade (couple) to form emergent faults

---
class:middle
##Conditions for killing the mutant (RIP model)
* Reach: test must reach the mutated statement
* Infect: test input data should infect the program state
* Propagate: incorrect program state must propagate to the output and checked
---
class:middle
##Equivalent mutants
the mutant is behaviorally equivalent to the original

hard to detect them
* DEM - Detecting
* SEM - Suggesting
* AEMG - Avoiding
---
class:middle
##Traditional mutation operators
* Statement deletion
* Statement duplication / insertion
* Replacement of boolean expression with true/false
* Replacement of arithmetic operations with others (+, -, *, /)
* Replacement of boolean relations with others (>, >=, <, <=, ==)
* Replacement of variables with other variables in scope
---
class: center, middle, inverse
##Wouldn't it be great to automate this?
"Well that sounds like an awesome idea!" - Sane Programmer

???
Mutation testing is meant to be automated! 

Luckily some folks have already thought about this, and there are several mutation testing tools available out there.
---
class: center, middle, inverse
##PIT
Real world mutation testing

???
One of these tools is called PIT.

Originally developed for running tests in parallel (PIT stands for Parallel Independent Tests), it grew into something much larger and bolder.
Author and Maintainer is Henry Coles.
---
class: middle
##Why use PIT?


"PIT runs your unit tests automatically against automatically modified versions of your application code" - pitest.org

---
class: middle
##Why use PIT?

* fast - can analyse in minutes what would take earlier systems days
* easy to use - works with ant, maven, gradle and others
* actively developed
* actively supported

???
* fast - can analyse in minutes what would take earlier systems days
* easy to use - works with ant, maven, gradle and others
* has jenkins and sonarqube plugins too
* active development - looking at the github project is quite active
* before moving to github, it didn't develop too fast (it was basically one man show)
* since moving to github the project picked up some speed and visibility
* actively supported - again, check how many issues they resolved since the project moved to github.
---
Demo time!
---
class: center, middle, inverse
#Conclusions
---
class: middle
##Conclusions
* Mutation testing is a brute force method to test the quality of a test suite
* PIT is a powerful tool, that that improves on that with ideas to make it less brute-force
* Mutation testing is very useful if you value code quality.
* PIT is mature enough to start using it today
---
class: center, middle, inverse
# Mind Blown

.center[![Mind Blown!][mind-blown]]

[mind-blown]: images/tim-and-eric-mind-blown.gif "Mind Blown!"

---
## Relevant Links

* http://reegnz.github.io/pitest-demo/
* https://github.com/reegnz
* http://pitest.org/
* http://vimeo.com/89083982
* http://googletesting.blogspot.hu/2010/07/code-coverage-goal-80-and-no-less.html
* http://blog.frankel.ch/your-code-coverage-metric-is-not-meaningful
* http://johnpwood.net/2008/12/30/why-code-coverage-alone-doesnt-mean-squat/
* http://en.wikipedia.org/wiki/Mutation_testing
* http://www0.cs.ucl.ac.uk/staff/mharman/exe10.html
