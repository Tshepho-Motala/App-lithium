export interface TableContract<T = any> {
  data: T[]
  draw: string
  recordsTotal: number
  recordsFiltered: number

  // add(item: T): void
}

export interface PageContract<T = any> {
  content: T[]
  pageable: Pageable
  last: boolean
  totalElements: number
  totalPages: number
  size: number
  number: number
  sort: Sort
  first: boolean
  numberOfElements: number
  empty: boolean
}

export interface Pageable {
  sort: Sort
  offset: number
  pageNumber: number
  pageSize: number
  unpaged: boolean
  paged: boolean
}

export interface Sort {
  empty: boolean
  sorted: boolean
  unsorted: boolean
}

// TODO PROM Move this to interfaces
