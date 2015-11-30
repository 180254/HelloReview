package pl.p.lodz.iis.hr.services;

import com.jayway.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.p.lodz.iis.hr.utils.ExceptionUtils;
import pl.p.lodz.iis.hr.utils.IRunner2;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.TimeUnit;

/**
 * During GH repository cloning temp subdirectory are used.<br/>
 * Some of these subdirectory may not be deleted, if some case of processing errors.<br/>
 * This class is to cover directory remove logic.
 */
@Service
public class GHTaskDirRemove implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(GHTaskDirRemove.class);

    private String dirPath;

    public GHTaskDirRemove() {
    }

    public GHTaskDirRemove(String dirPath) {
        this.dirPath = dirPath;
        LOGGER.info("{} Directory remove scheduled.", dirPath);
    }

    @Override
    public void run() {
        LOGGER.info("{} Directory removing.", dirPath);

        IRunner2 deleteRunner = () -> deleteDirectoryNIOWait(Paths.get(dirPath));
        boolean success = !ExceptionUtils.isExceptionThrown2(deleteRunner);

        LOGGER.info("{} Directory removing status succeeded = {}", dirPath, success);
    }

    public void deleteDirectoryNIO(Path path) throws IOException {
        Files.walkFileTree(path, new GHTaskDirRemove.DeleteFileVisitor());
    }

    public void deleteDirectoryNIOWait(Path path) throws IOException {
        IRunner2 deleteNIORunner = () -> deleteDirectoryNIO(path);

        try {
            Awaitility.await("directory is successfully deleted")
                    .atMost(10L, TimeUnit.SECONDS)
                    .pollDelay(3L, TimeUnit.SECONDS)
                    .pollInterval(3L, TimeUnit.SECONDS)
                    .until(() -> !ExceptionUtils.isExceptionThrown2(deleteNIORunner));
        } catch (Throwable ex) {
            throw new IOException(ex);
        }
    }

    private static class DeleteFileVisitor extends SimpleFileVisitor<Path> {

        private static final Logger LOGGER1 = LoggerFactory.getLogger(GHTaskDirRemove.DeleteFileVisitor.class);

        DeleteFileVisitor() {
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

            setWritable(file);
            Files.delete(file);

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            if (exc == null) {

                Files.delete(dir);
                return FileVisitResult.CONTINUE;

            } else {
                throw exc;
            }
        }

        private void setWritable(Path pathToBeWritable) {
            if (!new File(pathToBeWritable.toString()).setWritable(true)) {
                LOGGER1.warn("Failed to set {} as writable.", pathToBeWritable);
            }
        }
    }
}
