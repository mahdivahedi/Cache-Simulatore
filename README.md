# Cache-Simulatore

This Project is Simulator of Cache. it will detect the misses and writes in main memory and it takes inputs like this:

* 16 - 0 - 8 - wb - wa

* 256
* 0 00000 
* 0 10000 
* 0 00000 
* 0 20000 
* 0 30000 
* 0 00000 
* 0 10000 
* 0 20000 
* 0 30000 

first  is block size
second is cache type (Harvard[0] or von neumann [1])
third  is associativity of cache
fourth and fifth are write policy and allocation policy of it.

second line of input include the size of cache if cache type was Harvard OW the first parameter
                  is instruction's cache's size then second parameter is data's cache's size.

third line and after it include the exmples of data fetching and writing ( 0 for load data, 1 for write data, 2 for fetch instructions )
