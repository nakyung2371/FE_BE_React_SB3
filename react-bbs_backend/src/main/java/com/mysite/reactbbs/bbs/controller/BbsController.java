package com.mysite.reactbbs.bbs.controller;

import java.util.Date;
import com.mysite.reactbbs.bbs.dto.request.CreateBbsRequest;
import com.mysite.reactbbs.bbs.dto.request.BbsListRequest;
import com.mysite.reactbbs.bbs.dto.request.UpdateBbsRequest;
import com.mysite.reactbbs.bbs.dto.response.BbsListResponse;
import com.mysite.reactbbs.bbs.dto.response.BbsResponse;
import com.mysite.reactbbs.bbs.dto.response.CreateBbsResponse;
import com.mysite.reactbbs.bbs.dto.response.DeleteBbsResponse;
import com.mysite.reactbbs.bbs.dto.response.UpdateBbsResponse;
import com.mysite.reactbbs.bbs.service.BbsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController         // RequestBody : Client에서 JSON를 받아서 Java의 객체에 넣음  , 
						// ResponseBody : Server의 객체를 JSON 형태로 변환해서 Client 에게 전송 
@RequestMapping("/bbs")
public class BbsController {

	
	// DI : 생성자 주입 , 
	private final BbsService service;

	public BbsController(BbsService service) {
		this.service = service;
	}

	/* [GET /bbs?choice={choice}&search={search}&page={page}] 게시글 목록 API */
	@GetMapping
	public ResponseEntity<BbsListResponse> getBbsList(@ModelAttribute BbsListRequest req){
		System.out.println("BbsController getBbsList() " + new Date());
		
		return ResponseEntity.ok(service.getBbsList(req));
	}

	/* [GET /bbs/{seq}?readerId={id}] 게시글 상세 API */
	@GetMapping("/{seq}")
	public ResponseEntity<BbsResponse> getBbs(@PathVariable("seq")  Integer seq, @RequestParam("readerId") String readerId) {
		System.out.println("BbsController getBbs() " + new Date());

		return ResponseEntity.ok(service.getBbs(seq, readerId));
	}

	/* [POST] /bbs 게시글 작성 */
	@PostMapping
	public ResponseEntity<CreateBbsResponse> createBbs(@RequestBody CreateBbsRequest req) {
		System.out.println("BbsController createBbs " + new Date());
		
		return ResponseEntity.ok(service.createBbs(req));
	}

	/* [POST] /bbs/{parentSeq}/answer 게시글 답글 작성  */
	@PostMapping("/{parentSeq}/answer")
	public ResponseEntity<CreateBbsResponse> createBbsAnswer(@PathVariable("parentSeq") Integer parentSeq, @RequestBody CreateBbsRequest req) {
		System.out.println("BbsController createBbsAnswer " + new Date());

		return ResponseEntity.ok(service.createBbsAnswer(parentSeq, req));
	}

	/* [PATCH] /bbs/{seq} 게시글 수정  */
	// TODO - 수정하는 사람 ID 확인
	@PatchMapping("/{seq}")
	public ResponseEntity<UpdateBbsResponse> updateBbs(@PathVariable("seq") Integer seq, @RequestBody UpdateBbsRequest req) {
		System.out.println("BbsController updateBbs " + new Date());

		return ResponseEntity.ok(service.updateBbs(seq, req));
	}

	/* [DELETE] /bbs/{seq} 게시글 삭제  */
	@DeleteMapping("/{seq}")
	public ResponseEntity<DeleteBbsResponse> deleteBbs(@PathVariable("seq") Integer seq) {
		System.out.println("BbsController deleteBbs " + new Date());

		return ResponseEntity.ok(service.deleteBbs(seq));
	}
}








