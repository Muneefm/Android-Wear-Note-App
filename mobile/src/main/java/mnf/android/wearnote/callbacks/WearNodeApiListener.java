package mnf.android.wearnote.callbacks;

import com.google.android.gms.wearable.Node;

import java.util.List;

/**
 * Created by muneef on 15/06/17.
 */

public interface WearNodeApiListener {
     void onNodeConnected(List<Node> nodeList);
}
