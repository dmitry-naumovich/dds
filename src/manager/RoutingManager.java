package manager;

import com.naumovich.domain.Node;
import com.naumovich.table.AddressTable;

public abstract interface RoutingManager {
	
	void distributeChunks(Node owner, AddressTable addrTable);

}
