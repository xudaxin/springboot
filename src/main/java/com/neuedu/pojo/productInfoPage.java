package com.neuedu.pojo;

public class productInfoPage {
    private int currentPage;
    private int totalRecords;
    private int pageSize;
    private int totalPages;

    public productInfoPage() {
    }

    public productInfoPage(int currentPage, int totalRecords, int pageSize, int totalPages) {
        this.currentPage = currentPage;
        this.totalRecords = totalRecords;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    @Override
    public String toString() {
        return "productInfoPage{" +
                "currentPage=" + currentPage +
                ", totalRecords=" + totalRecords +
                ", pageSize=" + pageSize +
                ", totalPages=" + totalPages +
                '}';
    }
}
