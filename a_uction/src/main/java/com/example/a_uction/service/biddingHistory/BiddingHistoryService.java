package com.example.a_uction.service.biddingHistory;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.exception.constants.ErrorCode;
import com.example.a_uction.model.auction.dto.AuctionDto;
import com.example.a_uction.model.auction.repository.AuctionRepository;
import com.example.a_uction.model.biddingHistory.dto.BiddingHistoryDto;
import com.example.a_uction.model.biddingHistory.entity.BiddingHistoryEntity;
import com.example.a_uction.model.biddingHistory.repository.BiddingHistoryRepository;
import com.example.a_uction.model.user.entity.UserEntity;
import com.example.a_uction.model.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BiddingHistoryService {
    private final BiddingHistoryRepository biddingHistoryRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;

    private UserEntity getUser(String userEmail) {
        return userRepository.getByUserEmail(userEmail);
    }


    public ArrayList<AuctionDto.Response> getAllAuctions(String userEmail){
        Long userId = getUser(userEmail).getId();
        Set<Long> auctionIdSet = new HashSet<>();
        List<BiddingHistoryEntity> biddingHistoryList = biddingHistoryRepository.getAllByBidderId(userId);
        if (biddingHistoryList.size() == 0){
            return new ArrayList<>();
        }
        biddingHistoryList.stream().filter(x -> Objects.equals(x.getBidderId(), userId)).collect(Collectors.toList());
        for (BiddingHistoryEntity biddingHistory : biddingHistoryList){
            auctionIdSet.add(biddingHistory.getAuctionId());
        }
        ArrayList<AuctionDto.Response> auctionList = new ArrayList<>();
        for (Long auctionId : auctionIdSet){
            auctionList.add(
                    new AuctionDto.Response().fromEntity(auctionRepository.findById(auctionId)
                    .orElseThrow(() -> new AuctionException(ErrorCode.AUCTION_NOT_FOUND))
            ));
        }
        return auctionList;
    }


    public List<BiddingHistoryDto.Response> getAllBiddingHistoryByAuctionId(Long auctionId) {
        ArrayList<BiddingHistoryDto.Response> biddingHistories = new ArrayList<>();
        List<BiddingHistoryEntity> biddingHistoryEntityList =  biddingHistoryRepository.findAllByAuctionIdOrderByCreatedDateDesc(auctionId);
        for (BiddingHistoryEntity biddingHistory: biddingHistoryEntityList){
            biddingHistories.add(new BiddingHistoryDto.Response().fromEntity(biddingHistory,
                    auctionRepository.findById(biddingHistory.getAuctionId()).get().getItemName(),
                    userRepository.findById(biddingHistory.getBidderId()).get().getUserEmail()));
        }
        return biddingHistories;
    }
}
