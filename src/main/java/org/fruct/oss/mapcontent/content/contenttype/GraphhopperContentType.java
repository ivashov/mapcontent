package org.fruct.oss.mapcontent.content.contenttype;

import android.location.Location;

import org.fruct.oss.mapcontent.content.ContentItem;
import org.fruct.oss.mapcontent.content.ContentManagerImpl;
import org.fruct.oss.mapcontent.content.utils.DirUtil;
import org.fruct.oss.mapcontent.content.utils.Region;
import org.fruct.oss.mapcontent.content.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class GraphhopperContentType implements ContentType {
	private static final Logger log = LoggerFactory.getLogger(GraphhopperContentType.class);

	public GraphhopperContentType() {
	}

	@Override
	public void unpackContentItem(ContentItem contentItem, String contentItemPackageFile,
								  String unpackedPath) throws IOException {
		log.info("Extracting graphhopper archive {}", unpackedPath);

		File outDir = new File(unpackedPath);
		DirUtil.unzip(new File(contentItemPackageFile), outDir);
	}

	@Override
	public Region extractRegion(ContentItem item, String contentItemPackageFile) {
		ZipFile zipFile = null;
		try {
			File file = new File(contentItemPackageFile);

			zipFile = new ZipFile(file, ZipFile.OPEN_READ);
			ZipEntry entry = zipFile.getEntry("polygon.poly");

			return new Region(zipFile.getInputStream(entry));
		} catch (IOException e) {
			return null;
		} finally {
			Utils.silentClose(zipFile);
		}
	}

	@Override
	public boolean checkRegion(ContentItem item, String contentItemPackageFile, Location location) {
		// Normally shouldn't be called
		return extractRegion(item, contentItemPackageFile).testHit(location.getLatitude(), location.getLongitude());
	}

	@Override
	public String getName() {
		return ContentManagerImpl.GRAPHHOPPER_MAP;
	}

}
