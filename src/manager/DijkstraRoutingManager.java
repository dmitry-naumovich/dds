package manager;

import java.util.Iterator;
import java.util.List;

import com.naumovich.domain.Chunk;
import com.naumovich.domain.Node;
import com.naumovich.domain.message.ChunkMessage;
import com.naumovich.domain.message.Message;
import com.naumovich.network.MessageContainer;
import com.naumovich.table.AddressTable;
import com.naumovich.util.Dijkstra;
import com.naumovich.util.tuple.FourTuple;

public class DijkstraRoutingManager implements RoutingManager {
	
	public int amountOfFindingPath;
	
	@Override
	public void distributeChunks(Node owner, AddressTable addrTable) {
		Iterator<FourTuple<Integer, Chunk, Node, Integer>> iterator = addrTable.iterator();
		while (iterator.hasNext()) {
			FourTuple<Integer, Chunk, Node, Integer> tableRow = iterator.next();
			
			Dijkstra dijAlg = new Dijkstra(); // Dijkstra defines the route to destination
			dijAlg.execute(owner); // Dijkstra works
			List<Node> path = dijAlg.getPath(tableRow.third);
			amountOfFindingPath++;
			System.out.println(owner.getLogin() + ": I send " + tableRow.second.getChunkName() + " to " + 
								tableRow.third.getLogin() + ". The way is: " + path);
			if (path != null) {
				Message msg = new ChunkMessage(path, tableRow.second); 
				msg.excludeFirstNodeFromPath();
				MessageContainer.addMsg(msg);
			}
			
			// moreover, можно для результатов забабахть что-то вроде подсчета, как распределяются копии
			// например, на узел 5 попало две копии фрагмента 3
			// или вероятность того, что на один узел попадут все фрагменты файла
			// (без копий, просто, например, все 8 штук)... было бы интересно
		}

	}
	
	

}
