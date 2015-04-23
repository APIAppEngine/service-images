package apiserver.services.images.services.grid;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.internal.cluster.ClusterGroupAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

/**
 * Configuration for classes that use the Grid Gain Executor Service to invoke a method on a remote server
 *
 * Created by mnimer on 6/10/14.
 */
@Service
public class GridService implements Serializable
{
    @Autowired
    private Ignite grid = null;



    public ExecutorService getColdFusionExecutor() throws IgniteException
    {
        // Get grid-enabled executor service for nodes where attribute 'worker' is defined.
        ClusterGroup projection = grid.cluster().forNodeId(UUID.fromString("5d59104a-674b-4cec-88a8-86264d02641b"));//forAttribute("ROLE", "worker-coldfusion");

        if( projection.nodes().size() == 0 ) {  //todo, test that it returns null if CF is not running
            throw new IgniteException("connector-coldfusion Grid Node is not running or accessible");
        }

        return ((ClusterGroupAdapter) projection).executorService();
    }

}
