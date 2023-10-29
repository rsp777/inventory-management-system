package com.pawar.inventory.repository.lpn;

import com.pawar.inventory.model.Item;
import com.pawar.inventory.model.Lpn;

public interface LpnRepository {
	
	public Lpn createLpn(Lpn lpn, Item item);
	public Iterable<Lpn> getfindAllLpns();
	public Lpn getLpnByName(String lpn_name);
	public Lpn findLpnById(int lpn_id);
	public Lpn updateLpnByLpnId(int lpn_id, Lpn lpn);
	public Lpn updateLpnByLpnBarcode(String lpn_name, Lpn lpn);
	public Lpn deleteLpnByLpnId(int lpn_id);
	public Lpn deleteLpnByLpnBarcode(String lpn_name);
	
}
