package com.pawar.inventory.repository.grp;

import com.pawar.inventory.exceptions.GrpAlreadyExistsException;
import com.pawar.inventory.model.Grp;

public interface GrpRepository {
	Grp createGrp(Grp newGrp) throws GrpAlreadyExistsException;
	String generateBarcode(Grp grp);
	Iterable<Grp> getfindAllGrps();
	Grp getGrpByName(String grpName);
	Grp getGrpByDesc(String grpDesc);
	Grp getGrpById(int grp_id);
	Grp updateGrpById(int grp_id, Grp grp);
	Grp updateGrpByName(String grp_name, Grp grp);
	Grp deleteGrpById(int grp_id);
	Grp deleteGrpByName(String grp_name);
}
