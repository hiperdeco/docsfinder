package io.github.hiperdeco.docsfinder.controller;

import java.io.File;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

import io.github.hiperdeco.docsfinder.entity.Repository;
import io.github.hiperdeco.docsfinder.entity.RepositoryStatus;
import io.github.hiperdeco.docsfinder.entity.RepositoryType;

public class RepositoryFetcher {
	
	private Repository repository;
	
	public RepositoryFetcher(Repository repository) {
		this.repository = repository;
	}
	
	public void execute() throws Exception {
		if (repository.getStatus().equals(RepositoryStatus.INDEXING)) return;
		if (repository.getType().equals(RepositoryType.SVN)) {
		    fetchSVN();
			
		}
	}
	
	private void fetchSVN() throws Exception{
		BasicAuthenticationManager auth = BasicAuthenticationManager.newInstance(repository.getUser(), CryptoUtil.decode(repository.getPassword()).toCharArray());
		SVNClientManager ourClientManager =  SVNClientManager.newInstance(null,auth);
		SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
		long revision = 0L;
		String remoteURL = repository.getRemoteURL();
		String destPath = repository.getLocalDirectory();
		File destPathFile = new File(destPath);
	
		if (!destPathFile.exists()) destPathFile.mkdirs();
		
		if (!repository.getStatus().equals(RepositoryStatus.INDEXED) && !repository.getStatus().equals(RepositoryStatus.INDEXING)){
			revision = updateClient.doCheckout(SVNURL.parseURIEncoded(remoteURL), destPathFile, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY, false);
		}else {
			revision = updateClient.doUpdate(destPathFile, SVNRevision.HEAD, SVNDepth.INFINITY, false, true);
		}
		
		this.repository = (Repository) JPAUtil.findById(Repository.class, this.repository.getId());
		this.repository.setRevision(String.valueOf(revision));
		JPAUtil.update(this.repository);
		 
		
	}

}
