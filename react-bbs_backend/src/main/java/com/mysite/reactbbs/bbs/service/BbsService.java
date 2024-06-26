package com.mysite.reactbbs.bbs.service;

import java.util.List;
import java.util.Objects;
import com.mysite.reactbbs.bbs.dao.BbsDao;
import com.mysite.reactbbs.bbs.domain.Bbs;
import com.mysite.reactbbs.bbs.dto.param.BbsCountParam;
import com.mysite.reactbbs.bbs.dto.param.BbsListParam;
import com.mysite.reactbbs.bbs.dto.param.CreateBbsAnswerParam;
import com.mysite.reactbbs.bbs.dto.param.CreateBbsParam;
import com.mysite.reactbbs.bbs.dto.param.CreateReadCountParam;
import com.mysite.reactbbs.bbs.dto.param.UpdateBbsParam;
import com.mysite.reactbbs.bbs.dto.request.BbsListRequest;
import com.mysite.reactbbs.bbs.dto.request.CreateBbsRequest;
import com.mysite.reactbbs.bbs.dto.request.UpdateBbsRequest;
import com.mysite.reactbbs.bbs.dto.response.BbsListResponse;
import com.mysite.reactbbs.bbs.dto.response.BbsResponse;
import com.mysite.reactbbs.bbs.dto.response.CreateBbsResponse;
import com.mysite.reactbbs.bbs.dto.response.DeleteBbsResponse;
import com.mysite.reactbbs.bbs.dto.response.UpdateBbsResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BbsService {

	private final BbsDao dao;

	public BbsService(BbsDao dao) {
		this.dao = dao;
	}

	/* 게시글 조회 */
	public BbsListResponse getBbsList(BbsListRequest req) {
		BbsListParam param = new BbsListParam(req);
		param.setPageParam(req.getPage(), 10);

		List<Bbs> bbsList = dao.getBbsSearchPageList(param);
		//int pageCnt = dao.getBbsCount(new BbsCountParam(req));
		int pageCnt = dao.getBbsCount(param);
		
		System.out.println("pageCnt : " + pageCnt);

		return new BbsListResponse(bbsList, pageCnt);
	}

	/* 특정 글 */
	/* 조회수 수정 */
	public BbsResponse getBbs(Integer seq, String readerId) {
		// 로그인 한 사용자의 조회수만 카운팅
		if (!readerId.isEmpty()) {
			CreateReadCountParam param = new CreateReadCountParam(seq, readerId);
			Integer result = dao.createBbsReadCountHistory(param); // 조회수 히스토리 처리 (insert: 1, update: 2)
			if (result == 1) {
				Integer updatedRecordCount = dao.increaseBbsReadCount(seq); // 조회수 증가
			}
		}

		return new BbsResponse(dao.getBbs(seq));
	}

	/* 글 추가 */
	public CreateBbsResponse createBbs(CreateBbsRequest req) {
		CreateBbsParam param = new CreateBbsParam(req);
		
		//System.out.println("SEQ 의 값 : " + param.getSeq());
		
		dao.createBbs(param);
		
		//System.out.println("DAO 호출후 SEQ 의 값 : " + param.getSeq());
		
		return new CreateBbsResponse(param.getSeq());
	}

	/* 답글 추가 */
	public CreateBbsResponse createBbsAnswer(Integer parentSeq, CreateBbsRequest req) {
		Integer updatedRecordCount = dao.updateBbsStep(parentSeq);
		Integer bbsAnswerCount = dao.getBbsAnswerCount(parentSeq);
		// TODO - 예외처리
		if (!Objects.equals(updatedRecordCount, bbsAnswerCount)) {
			System.out.println("BbsService createBbsAnswer: Fail update parent bbs step !!");
			return null;
		}

		CreateBbsAnswerParam param = new CreateBbsAnswerParam(parentSeq, req);
		dao.createBbsAnswer(param);
		return new CreateBbsResponse(param.getSeq());
	}

	/* 글 수정 */
	public UpdateBbsResponse updateBbs(Integer seq, UpdateBbsRequest req) {
		Integer updatedRecordCount = dao.updateBbs(new UpdateBbsParam(seq, req));
		return new UpdateBbsResponse(updatedRecordCount);
	}

	/* 게시글 삭제 */
	public DeleteBbsResponse deleteBbs(Integer seq) {
		Integer deletedRecordCount = dao.deleteBbs(seq);
		return new DeleteBbsResponse(deletedRecordCount);
	}
}






