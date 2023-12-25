package com.myblog.blogapp.controller;

import com.myblog.blogapp.entities.Post;
import com.myblog.blogapp.payload.PostDto;
import com.myblog.blogapp.payload.PostResponse;
import com.myblog.blogapp.service.PostService;
import com.myblog.blogapp.utils.AppConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PreAuthorize("hasRole('ADMIN')") //12-1-23, to access to ADMIN only
    @PostMapping
    public ResponseEntity<Object> createPost(@Valid  @RequestBody PostDto postDto, BindingResult bindingResult) {  //this method has ResponseEntity<> as return type
        if(bindingResult.hasErrors()){
            return new ResponseEntity<>(bindingResult.getFieldError().getDefaultMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //2-1-23. for implementing validation we should have to Create object of BindingResult and change the generic of ResponseEntity to Object
                  // and add an if condition when validation happens and the input is not correct then it will return some message to the postman

        return new ResponseEntity<>(postService.createPost(postDto), HttpStatus.CREATED); //ResponseEntity is used to sending response back to Postman
        //like 201 created
        //2-1-23, @Valid to be added before @RequestBody so that validation can happen
    }

//   // Get all posts
//    //Now Modify the GetMapping with PageNO and PageSize, to implement Pagination
//    //to send Response like total no. of pages, pageNo, pageSize. we have to create Response Class in payload package
        //    @GetMapping   //localhost:8080/api/posts
        //    public List<PostDto> getAllPosts(){  //6-12-22
        //
        //        return postService.getAllPosts();
        //    }
//            @RequestParam(value = "sortBy", defaultValue=AppConstants.DEFAULT_SORT_BY,required= false) String sortBy,    //you should have to give default value as id
//            @RequestParam(value = "sortDir", defaultValue=AppConstants.DEFAULT_SORT_DIR, required=false) String sortDir    //sortDir means sorting sorting based on direction like ascending or descending                                                                                    // if you didn't sort on any basis it should work on id by default


    //Get post by id
    ////http://localhost:8080/api/posts/1
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable("id") long id) {  //6-12-22
        //whenever you want to return a status code, the method return type should be ResponseEntity<>
        //on date 9-12-22 we have learned how to handle the exception and return backed the RepsonseEntity
        return ResponseEntity.ok(postService.getPostById(id));
    }

    // update post by id rest api
    //http://localhost:8080/api/posts/1
    @PreAuthorize("hasRole('ADMIN')")  //12-1-23, to access to ADMIN only
    @PutMapping("/{id}")  //9-12-22
    public ResponseEntity<PostDto> updatePost(@RequestBody PostDto postDto, @PathVariable("id") long id) { //id no. provided by url, postDto is supplied by JSON
        PostDto dto = postService.updatePost(postDto, id);
        return new ResponseEntity<>(dto, HttpStatus.OK);    //if you want to supply status and parameter, we have to create object
    }

    // delete post rest api
    //http://localhost:8080/api/posts/1
    @PreAuthorize("hasRole('ADMIN')")  //12-1-23, to access to ADMIN only
    @DeleteMapping("/{id}")  //9-12-22
    public ResponseEntity<String> deletePost(@PathVariable("id") long id) {

        postService.deletePost(id);
        return new ResponseEntity<>("Post Entity Deleted Successfully.", HttpStatus.OK);  //if we are retuning more than one parameter in the ResponseEntity then we will have to use new keyword

    }


//    @GetMapping //12-12-22     //localhost:8080/api/posts?pageNo=0&pageSize-10
//    public List<PostDto> getAllPosts(
//            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
//            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
//            //for date 12-12-22, we are implementing the pagination concept based on pageNo and pageSize
//
//    ) {
//        return postService.getAllPosts(pageNo, pageSize);
//    }

    @GetMapping   //14-12-22, Sending Response and implementing Sorting based on column name
                 //17-12-22, implementing sorting based on direction
    public PostResponse getAllPosts(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIR, required = false) String sortDir  //17-12-22, implementing sorting based on direction
    ){
        return postService.getAllPosts(pageNo, pageSize, sortBy, sortDir);
    }


}
