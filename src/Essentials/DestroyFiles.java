/**
 * 
 */
/**
 * 
 */
package essentials;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;

/**
 * @author Maximilian
 *
 */
public class DestroyFiles {

	File file;
	Random rand;

	public DestroyFiles(File f) {
		file = f;
		rand = new Random();
	}

	/**
	 * Will overwrite the file with random bytes. .wipe() is probably better and
	 * more efficent, but I'm not sure if it works.
	 * 
	 * 
	 * @param times
	 *            Number of iterations
	 * @throws IOException
	 */

	public boolean shred(int times) {
		long length = file.length();
		byte[] content = new byte[(int) length];
		FileOutputStream out;
		for (int i = 0; i < times; i++) {

			rand.nextBytes(content);

			try {
				out = new FileOutputStream(file);
				out.write(content);
				out.close();
			} catch (IOException e) {
				return false;
			}
		}
		return true;

	}

	/**
	 * Will overwrite the file with random bytes. Is probably better then
	 * .shred, but .shred works for sure, this doesn't.
	 * 
	 * @param times
	 *            Number of iterations
	 */
	public boolean wipe(int times) {
		for (int i = 0; i < times; i++) {
			try {
				RandomAccessFile rwFile = new RandomAccessFile(file, "rw");
				try {
					FileChannel rwChannel = rwFile.getChannel();
					int numBytes = (int) rwChannel.size();
					MappedByteBuffer buffer = rwChannel.map(
							FileChannel.MapMode.READ_WRITE, 0, numBytes);
					buffer.clear();
					byte[] randomBytes = new byte[numBytes];
					rand.nextBytes(randomBytes);
					buffer.put(randomBytes);
					buffer.force();
					// will already write to the disk
				} finally {
					rwFile.close();
				}
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Will change random bytes to random values.
	 * 
	 * @param howLong
	 *            How long the file should burn. 1 will burn approximately half
	 *            the file.
	 * @return
	 */
	public boolean burn(float howLong) {
		try {
			RandomAccessFile raf = new RandomAccessFile(file.getPath(), "rw");
			for (int i = 0; i < (int) (raf.length() * howLong); i++) {
				raf.seek((int) (Math.random() * ((raf.length()) + 1)));
				byte[] b = new byte[1];
				rand.nextBytes(b);
				raf.write(b);
			}
			raf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;

	}

	public void delete() {

		file.delete();

	}

	/**
	 * Overwrite the file with a given byte
	 * 
	 * @param data
	 *            The data to write in the file
	 * @return
	 */
	public boolean overwriteWith(byte data) {
		long length = file.length();
		byte[] content = new byte[(int) length];
		for (byte b : content) {
			b = data;
		}

		FileOutputStream out;

		try {
			out = new FileOutputStream(file);
			out.write(content);
			out.close();
		} catch (IOException e) {
			return false;
		}

		return true;

	}

	public boolean secureDelete() {

		byte[] b = new byte[1];
		rand.nextBytes(b);

		long time = System.currentTimeMillis();
		shred(1);
		System.out.println(System.currentTimeMillis() - time);
		time = System.currentTimeMillis();
		overwriteWith(b[0]);
		System.out.println(System.currentTimeMillis() - time);
		time = System.currentTimeMillis();
		wipe(1);
		System.out.println(System.currentTimeMillis() - time);
		time = System.currentTimeMillis();
		burn(1);
		System.out.println(System.currentTimeMillis() - time);
		time = System.currentTimeMillis();

		return true;

	}

	public static void main(String[] args) throws IOException {
		new DestroyFiles(new File("E://file8.txt")).secureDelete();

	}

}