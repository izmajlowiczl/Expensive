package pl.expensive.storage

interface Storage<DBO> {
    /**
     * List all stored dbos. Result is null safe

     * @return Collection of all stored database objects or Collections.emptyList() for empty result.
     */
    fun list(): List<DBO>

    /**
     * Stores [Wallet] object.

     * @param dbo There is no validation of object performed. Check for nulls or invalid values before using.
     * *
     * @throws IllegalStateException In case of storing duplicate.
     */
    fun insert(dbo: DBO)
}
