package io.github.hiperdeco.docsfinder.main;

import java.util.Collection;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

public class TesteSvn01 {

	public static void main(String[] args) {
		
		BasicAuthenticationManager auth = BasicAuthenticationManager.newInstance("amoraes", "".toCharArray());
		SVNClientManager ourClientManager =  SVNClientManager.newInstance(null,auth);
		SVNUpdateClient updateClient = ourClientManager.getUpdateClient( );
		String url = "https://svn.cpqd.com.br/cpqd/etics/branches/dev/ETICS-REQUISITOS/requisitos/especificacoes/adm";
		String dstPath = "/home/andre/svn";
		try {
		 // long number = 	updateClient.doCheckout(SVNURL.parseURIEncoded(url), new File(dstPath), SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY, false);
		 // System.out.println(number);
		  
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//get the modified files
		try {
			SVNRepository repository = ourClientManager.getRepositoryPool().createRepository(SVNURL.parseURIEncoded(url),true );
			long revision = 85536;// repository.getLatestRevision();
			Collection logEntries = repository.log(new String[] {""}, null,  revision, revision, true, true);
			for (Object entry: logEntries) {
				SVNLogEntry logEntry = (SVNLogEntry) entry;
				if (logEntry.getChangedPaths().size()> 0) {
					for (String key : logEntry.getChangedPaths().keySet()) {
						System.out.println("chave: " + key  + " - " + logEntry.getChangedPaths().get(key).getType()
								+ " - " + logEntry.getChangedPaths().get(key).getPath());
					}
				}
			}
			repository.closeSession();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.exit(0);

	}

}
