package com.pawar.inventory.repository.asn;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.pawar.inventory.exceptions.ASNNotFoundException;
import com.pawar.inventory.exceptions.CategoryNotFoundException;
import com.pawar.inventory.exceptions.ItemNotFoundException;
import com.pawar.inventory.exceptions.LpnNotFoundException;
import com.pawar.inventory.model.ASN;
import com.pawar.inventory.model.Lpn;

import jakarta.persistence.NoResultException;

public interface ASNRepository {

	ASN createASN(ASN asn) throws ItemNotFoundException, CategoryNotFoundException, LpnNotFoundException, NoResultException, ASNNotFoundException;

	ASN getASNByName(String asnBrcd) throws NoResultException, ASNNotFoundException;

	String receiveAsn(ASN asn, List<Lpn> lpns) throws LpnNotFoundException, NoResultException, ASNNotFoundException;
}
