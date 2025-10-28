package com.pawar.inventory.service;

import java.io.IOException;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawar.inventory.constants.AsnStatusConstants;
import com.pawar.inventory.entity.ASNDto;
import com.pawar.inventory.exceptions.ASNNotFoundException;
import com.pawar.inventory.exceptions.CategoryNotFoundException;
import com.pawar.inventory.exceptions.ItemNotFoundException;
import com.pawar.inventory.exceptions.LpnNotFoundException;
import com.pawar.inventory.model.ASN;
import com.pawar.inventory.model.Lpn;
import com.pawar.inventory.repository.asn.ASNRepository;

import jakarta.persistence.NoResultException;

@Service
public class ASNService {

	private final static Logger logger = LoggerFactory.getLogger(ASNService.class);

	private static final String WMS_ASN_DATA_INCOMING = "WMS.ASN.DATA.INCOMING";

	private final ObjectMapper objectMapper;

	@Autowired
	private ASNRepository asnRepository;

	public ASNService() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
	}

	@Transactional
	@KafkaListener(topics = WMS_ASN_DATA_INCOMING, groupId = "consumer_group5")
	public void incomingASNListener(ConsumerRecord<String, String> consumerRecord, Acknowledgment ack)
			throws JsonMappingException, JsonProcessingException, ItemNotFoundException, CategoryNotFoundException {
		String key = consumerRecord.key();
		String value = consumerRecord.value();
		int partition = consumerRecord.partition();
		logger.info("Incoming payload : {}",value);
		ASNDto asnDto = objectMapper.readValue(value, ASNDto.class);
		ASN asn = convertAsnDtoToEntity(asnDto);
		List<Lpn> lpns = objectMapper.convertValue(asn.getLpns(),objectMapper.getTypeFactory().constructCollectionType(List.class, Lpn.class));
		String asnBrcd = asnDto.getAsnBrcd();
		logger.debug("value : {}", value);
		logger.debug("Consumed message : " + asnDto + " with key : " + key + " from partition : " + partition);
		logger.info("Incoming ASN : {}", asnBrcd);
		try {
			createASN(asnDto);
//			receiveAsn(asn, lpns);
		} catch (CategoryNotFoundException e) {
			logger.error("Category Not Found : {}", e.getMessage());

		} catch (ItemNotFoundException e) {
			logger.error("Item Not Found : {}", e.getMessage());
		} catch (LpnNotFoundException e) {
			logger.error("Lpn Not Found : {}", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error processing Kafka message: {}", e.getMessage());
		}
	}
	
	private ASN convertAsnDtoToEntity(ASNDto asnDto) throws ItemNotFoundException, CategoryNotFoundException {
		ASN  asn = new ASN(asnDto); 
		return asn;
	}
	
	@Transactional
	public ASN createASN(ASNDto asnDto) throws ItemNotFoundException, CategoryNotFoundException, LpnNotFoundException, NoResultException, ASNNotFoundException {
		ASN asn = new ASN(asnDto);
		logger.info("Creating new ASN : {}", asn.getAsnBrcd());
		logger.debug("Creating new ASN : {}", asn);
		ASN savedASN = asnRepository.createASN(asn);
		return savedASN;
	}

	@Transactional
	public boolean validateASN(String asnBrcd) throws NoResultException, ASNNotFoundException {
		ASN asn = getASNByName(asnBrcd);
		if (asn != null) {
			logger.info("ASN {} already present in the WMS.", asnBrcd);
			return true;
		} else {
			logger.info("ASN {} is not present in the WMS.", asnBrcd);
			return false;
		}
	}

	@Transactional
	public ASN getASNByName(String asnBrcd) throws NoResultException, ASNNotFoundException {
		return asnRepository.getASNByName(asnBrcd);
	}

	@Transactional
	public String receiveAsn(ASN asn, List<Lpn> lpns) throws LpnNotFoundException, NoResultException, ASNNotFoundException {
		// TODO Auto-generated method stub
		return asnRepository.receiveAsn(asn,lpns);
	}

	public List<ASN> getAsnByCategory(String category) {
		// TODO Auto-generated method stub
		return asnRepository.getAsnByCategory(category);
	}

}
