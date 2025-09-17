import { PropsWithChildren, ReactNode } from 'react';
import './DataTable.css';

type Column<T> = {
  header: string;
  accessor: (row: T) => ReactNode;
  width?: string;
};

type DataTableProps<T> = {
  columns: Column<T>[];
  data: T[];
  emptyMessage?: string;
};

export const DataTable = <T,>({ columns, data, emptyMessage }: PropsWithChildren<DataTableProps<T>>) => {
  if (!data.length) {
    return <div className="table-empty">{emptyMessage ?? 'データがありません'}</div>;
  }

  return (
    <div className="table-wrapper">
      <table>
        <thead>
          <tr>
            {columns.map((column) => (
              <th key={column.header} style={column.width ? { width: column.width } : undefined}>
                {column.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.map((row, index) => (
            <tr key={index}>
              {columns.map((column) => (
                <td key={column.header}>{column.accessor(row)}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};
