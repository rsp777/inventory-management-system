package com.pawar.inventory.repository.lpn;

import java.util.List;

import com.pawar.inventory.exceptions.CategoryNotFoundException;
import com.pawar.inventory.exceptions.ItemNotFoundException;
import com.pawar.inventory.exceptions.LpnNotFoundException;
import com.pawar.inventory.model.ASN;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.model.Lpn;

public interface LpnRepository {

	public Lpn createLpn(Lpn lpn, Item item)
			throws ItemNotFoundException, CategoryNotFoundException, LpnNotFoundException;

	public Iterable<Lpn> getfindAllLpns();

	public Lpn getLpnByName(String lpn_name) throws LpnNotFoundException;

	public Lpn findLpnById(int lpn_id);

	public Lpn updateLpnByLpnId(int lpn_id, Lpn lpn) throws ItemNotFoundException, CategoryNotFoundException;

	public Lpn updateLpnByLpnBarcode(String lpn_name, Lpn lpn, int adjustQty)
			throws ItemNotFoundException, CategoryNotFoundException, LpnNotFoundException;

	public Lpn deleteLpnByLpnId(int lpn_id);

	public Lpn deleteLpnByLpnBarcode(String lpn_name) throws LpnNotFoundException;

	public Lpn deallocateLpn(String lpnName) throws LpnNotFoundException;

	public List<Lpn> findLpnByCategory(String category) throws LpnNotFoundException;

	public void createLpn(Lpn lpn, Item item, String asnBrcd) throws ItemNotFoundException, LpnNotFoundException;


}
