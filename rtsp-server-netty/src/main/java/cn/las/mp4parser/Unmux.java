package cn.las.mp4parser;

import org.mp4parser.muxer.Movie;
import org.mp4parser.muxer.Track;
import org.mp4parser.muxer.builder.DefaultMp4Builder;
import org.mp4parser.muxer.container.mp4.MovieCreator;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collections;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/3/24
 */
public class Unmux {
    public static void main(String[] args) throws IOException {
//        File dest = File.createTempFile("Unmux", "main");
//        FileOutputStream fos = new FileOutputStream(dest);
//        IOUtils.copy(new URL("http://org.mp4parser.s3.amazonaws.com/examples/Cosmos%20Laundromat%20small.mp4").openStream(), fos);
//        fos.close();

        File dest=new File("C:\\Users\\andy\\Desktop\\1365070268951.mp4");

        Movie m = MovieCreator.build(dest.getAbsolutePath());
        DefaultMp4Builder builder = new DefaultMp4Builder();
        for (Track track : m.getTracks()) {
            Movie singleTrackMovie = new Movie(Collections.singletonList(track));
            builder.build(singleTrackMovie).writeContainer(new RandomAccessFile("C:\\Users\\andy\\Desktop\\"+track.getHandler() + "_" + track.getTrackMetaData().getTrackId() + ".mp4", "rw").getChannel());
        }
    }
}
