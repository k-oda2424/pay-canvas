export const formatYearMonthLabel = (yearMonth: string) => {
  const [year, month] = yearMonth.split('-');
  return `${Number(year)}年${Number(month)}月`;
};

export const availableYearMonths = ['2024-04', '2024-03', '2024-02'];
