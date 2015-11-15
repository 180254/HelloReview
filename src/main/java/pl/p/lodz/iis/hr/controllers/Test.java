package pl.p.lodz.iis.hr.controllers;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Test {

    public static void main(String[] args) throws IOException, GitAPIException {

        String[] branches = {"master", "refactorization"};
        for (String branch : branches) {

            FileUtils.deleteDirectory(new File("temp" + File.separator + "alamakota"));
            new File("temp" + File.separator + "alamakota").mkdir();

            Git.cloneRepository()
                    .setURI("https://github.com/180254/lab1_1.git")
                    .setDirectory(new File("temp" + File.separator + "alamakota"))
                    .setBranch(branch)
//                .setCredentialsProvider(new UsernamePasswordCredentialsProvider("***", "***"))
                    .call().close();
            FileUtils.deleteDirectory(new File("temp" + File.separator + "alamakota" + File.separator + ".git"));
//deleteNio(Paths.get("temp" + File.separator + "alamakota"+ File.separator +".git"));

            Git call = Git.init().setDirectory(new File("temp" + File.separator + "alamakota")).call();


            String[] list = new File("temp" + File.separator + "alamakota").list();
            AddCommand add = call.add();
            for (String s : list) {
                add.addFilepattern(s);
            }
            add.call();

            call.commit().setMessage("clone for peer review purposes").call();
            if (!Objects.equals(branch, "master"))
                call.branchCreate().setName(branch).call();

            call.push().setRemote("https://github.com/180254/test.git")
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider("180254", "svn78977")).call();
            call.close();

        }


    }
}

