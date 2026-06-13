package mihon.domain.extensionrepo.exception

class SaveExtensionRepoException(throwable: Throwable) :
    Exception("Error Saving Repository to Database", throwable)
