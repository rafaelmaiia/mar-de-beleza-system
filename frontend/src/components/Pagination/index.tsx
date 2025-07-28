import ReactPaginate from 'react-paginate';

type PaginationProps = {
  pageCount: number;
  onPageChange: (selectedItem: { selected: number }) => void;
};

export function Pagination({ pageCount, onPageChange }: PaginationProps) {
  return (
    <div className="mt-8 flex justify-center items-center">
      <ReactPaginate
        breakLabel="..."
        nextLabel="Próximo >"
        onPageChange={onPageChange}
        pageRangeDisplayed={3} // Quantos números de página mostrar no meio
        pageCount={pageCount}
        previousLabel="< Anterior"
        renderOnZeroPageCount={null}
        // Classes do Tailwind para estilizar
        containerClassName="flex items-center space-x-1"
        pageLinkClassName="px-4 py-2 bg-white rounded-md text-gray-700 hover:bg-gray-100 text-sm font-medium"
        previousLinkClassName="px-3 py-2 bg-white rounded-md text-gray-500 hover:bg-gray-100 text-sm font-medium"
        nextLinkClassName="px-3 py-2 bg-white rounded-md text-gray-500 hover:bg-gray-100 text-sm font-medium"
        activeLinkClassName="bg-indigo-600 text-white hover:bg-indigo-700"
        disabledLinkClassName="text-gray-300 cursor-not-allowed"
      />
    </div>
  );
}