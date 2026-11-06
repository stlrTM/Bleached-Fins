package stellar.ether.api

object Dependencies {
    fun <T> sortDependencies(items: Collection<T>, getDependencies: (T) -> Collection<T>): Collection<T> {
        val result = mutableListOf<T>()
        val visited = mutableSetOf<T>()
        val visiting = mutableSetOf<T>()

        fun visit(item: T) {
            if (item in visited) return
            if (item in visiting) {
                throw IllegalStateException("Cyclic dependency!")
            }

            visiting.add(item)

            for (dependency in getDependencies(item)) {
                if (dependency in items) {
                    visit(dependency)
                }
            }

            visiting.remove(item)
            visited.add(item)
            result.add(item)
        }

        for (item in items) {
            visit(item)
        }

        return result
    }
}