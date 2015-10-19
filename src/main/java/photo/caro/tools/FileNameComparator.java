package photo.caro.tools;

import java.io.File;
import java.util.Comparator;

public class FileNameComparator implements Comparator<File> {

	public int compare(File arg0, File arg1) {
		return arg0.getName().compareTo(arg1.getName());
	}

}
