/**
 * 
 */
package uk.ti;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.appl.playlist.PlayList;
import com.timeindexing.appl.playlist.IndexPropertiesItem;
import java.util.Properties;

/**
 * @author sclayman
 *
 */
public class PlayList1 {

    /**
     * @param args
     */
    public static void main(String[] args) {
	if (args.length != 1) {
	    error();
	} else {
	    String indexpath = args[0];
	    new PlayList1(indexpath);
	}
    }

    public static void error() {
	System.err.println("PlayList1 indexpath");
    }

    public PlayList1(String indexpath) {
	Properties openProperties = new Properties();
	openProperties.setProperty("indexpath", indexpath);

	TimeIndexFactory factory = new TimeIndexFactory();

	try {
	    IndexView index = factory.open(openProperties);

	    System.err.println("PlayList1: Object " + this.hashCode() + ". Thread " + Thread.currentThread().getName() + ". Index " + index.getID() + " opened");

	    // create a PlayList
	    PlayList playList = new PlayList();
		
	    // now make some PlayList items
	    IndexProperties item1 = new IndexProperties();
	    item1.put("starttime", "0:30");
	    item1.put("for", "0:22");

	    IndexProperties item2 = new IndexProperties();
	    item2.put("starttime", "1:30");
	    item2.put("for", "0:47");

	    playList.add(new IndexPropertiesItem(index, item1));
	    playList.add(new IndexPropertiesItem(index, item2));

	    System.out.println(playList);

	    factory.close(index);

	} catch (TimeIndexException ice) {
	    ice.printStackTrace();
	    System.exit(1);
	}

    }

	
	
}
