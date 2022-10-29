@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({ObjectMapper.class, <yourController>.class})
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class <YourClassName> {
    // use this to perform api requests
    @Autowired
		private MockMvc mockMvc;
    // for live testing no mocking
    @Autowired
    private <YourRepository> repo;

    // saved user id
    private static int savedUserId;
    // you are mocking the repository and testing the service layer
    // this is not applicable here, use the above code to test in integration mode.
    /*@Mock
    private <YourRepository> mockedRepo;*/
    
    // optionally used to create json objects
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        
    }


  // save a user with and put the id in static filed (savedUserId)
    @Test
	  @Order(1)
    public void saveStaffTest() throws Exception {
      final String link = "/user/save"; // link to your controller api end point
      User user = new User(10001L, "Alex", "alex.jamal@gmail.com", 10, LocalDate.of(2012, Month.APRIL, 13)); // define your default object here
      /***
       *  note: in your User class you might have id as auto generated, because of this you need two constructor one with id, and other without id
       * , or you will have an error.
       *  you can use objectmapper if you run to an error and just pass the object as json string.
      */
      
      // here you are performing save request and getting the result
      MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
						.post(link)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(user))) // this will make the object a json string
						.andExpect(MockMvcResultMatchers.status().isCreated()) // here is what important! it will check the response status, if created 201 the test will success, else it will fail
						.andReturn(); // returning the result
				String response = result.getResponse().getContentAsString();
				System.out.println("from response: "+response); // this is the response you passed on your controller method
				/**
        * knowing your response structure search for the user id
        */
				JsonNode root = objectMapper.readTree(response); 
				JsonNode id = root.get("id"); 
				savedUserId = id.asInt(); here you save the id in static field savedUserId, then you can pass this id on the next test (delete/update);
        
				System.out.println("id is: "+id);
    }
    @Test
    @Order(2)
    void shouldDeleteUser() throws IllegalStateException, Exception{
    // here delete the object, and assert the response state is ok, no need to return any value
    final String link = "/user/delete/"+savedUserId;
				this.mockMvc.perform(MockMvcRequestBuilders
						.post(link)
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(MockMvcResultMatchers.status().isOk()); // fail if the response is not 200, couldn't delete the object
        underTest.addNewUser(userCreateDto);
        final long id = 223;
        // when
        underTest.deleteUserById(id);
        //then
        verify(userRepository).deleteById(id);
    }
    
    /***************** save user method to add to controller ***************/
      @PostMapping("/user/save")
      private ResponseEntity<User> addNewUser(@RequestBody User user){
          try {
               userRepo.save(user); // can call the service if you want also
               return ResponseEntity.status(HttpStatus.CREATED).body(user);
          }catch(Exception e){
              System.out.println(e.getMessage()); 
          }
        
          return ResponseEntity.badRequest().body(null);
      }
      
      /***************** delete user method to add to controller ***************/
      @PostMapping("/user/delete/{userId});
      private ResponseEntity deleteUser(@PathVariable("userId) int userId){
          try {
              // you can add the logic to check if user exists first
              User user = userRepo.getById(userId);
              if(user != null){
              // delete the user if it exists
               userRepo.delete(user); // delete
               return ResponseEntity.OK(); // you can add a body response if you want to
          }catch(Exception e){
              System.out.println(e.getMessage()); 
          }
        
          return ResponseEntity.badRequest();
      }

}
