// copy this code to your controller and past it in the method where you want to have the circuit breaker
@RestController
@CrossOrigin
@RequestMapping("/api")
public class CircuitBreakerController {
  
  /**** resilience4j****/
	@CircuitBreaker(name = "circuitBreakerName", fallbackMethod = "testMethodfallback") // circuitbreaker name same as in application.properties
	@RateLimiter(name = "circuitBreakerName")
	@Bulkhead(name = "circuitBreakerName", fallbackMethod = "testMethodfallback") // fallback method should return the same entity as the original method
	@Retry(name = "circuitBreakerName")	
	
	@GetMapping("/testCircuitBreaker")
	  public ResponseEntity<?> testMethod() {
	    boolean serverUp = true; // use this to simulate a server
	    if(serverUUp){
	       return ResponseEntity.ok().body("worked");
	    }
	    return ResponseEntity.badRequest().body(null);
	  }
	  /******** the fallback methods ************/
	  public  ResponseEntity<?> testMethodfallback(CallNotPermittedException e) {
		System.out.println("server is down! please try again after some time");
		return ResponseEntity.internalServerError().body(null);
	   }
	public  ResponseEntity<?> testMethodfallback(Exception e) {
		System.out.println("server is down! please try again after some time");
		return ResponseEntity.internalServerError().body(null);
	}
}
