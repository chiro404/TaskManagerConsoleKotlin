package result

import model.Task

sealed class SearchResult {

    data class SearchSuccess(val listSearch: List<Task>) : SearchResult()
    data class SearchError(val message: String) : SearchResult()
    data class SearchNotFound(val id: Int) : SearchResult()
}