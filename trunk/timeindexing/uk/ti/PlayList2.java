/**
 * Test URIIndexPropertiesItem
 */
package uk.ti;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.appl.playlist.PlayList;
import com.timeindexing.appl.playlist.URIIndexPropertiesItem;
import java.util.Properties;
import java.net.URI;

/**
 * @author sclayman
 *
 */
public class PlayList2 {

    /**
     * @param args
     */
    public static void main(String[] args) {
	if (args.length != 1) {
	    error();
	} else {
	    String indexpath = args[0];
	    new PlayList2(indexpath);
	}
    }

    public static void error() {
	System.err.println("PlayList2 indexpath");
    }

    public PlayList2(String indexpath) {

	try {
	    // the index URI
	    URI indexURI = new URI("index", "", indexpath, null);

	    // create a PlayList
	    PlayList playList = new PlayList();
		
	    // now make some PlayList items
	    IndexProperties item1 = new IndexProperties();
	    item1.put("starttime", "0:30");
	    item1.put("for", "0:22");

	    IndexProperties item2 = new IndexProperties();
	    item2.put("starttime", "1:30");
	    item2.put("for", "0:47");

	    playList.add(new URIIndexPropertiesItem(indexURI, item1));
	    playList.add(new URIIndexPropertiesItem(indexURI, item2));

	    System.out.println(playList);

	} catch (Exception ice) {
	    ice.printStackTrace();
	    System.exit(1);
	}

    }

	
	
}
