import React, {Component} from 'react'
import {Link} from 'react-router-dom'

const defaultProps = {
    initialPage: 1,
    pageSize: 5
}

class Pagination extends Component {
    constructor(props) {
        super(props)
        this.state = { pager: {} }
    }

    componentWillMount() {
        if (this.props.items && this.props.items.length) {
            this.setPage(undefined, this.props.initialPage)
        }
    }

    componentDidUpdate(prevProps, prevState) {
        if (this.props.items !== prevProps.items) {
            this.setPage(undefined, this.props.initialPage)
        }
    }

    setPage = (e, page) => {

        if(e) e.preventDefault()

        let { items, pageSize } = this.props
        let pager = this.state.pager

        if (page < 1 || page > pager.totalPages) return

        pager = this.getPager(items.length, page, pageSize)
        let pageOfItems = items.slice(pager.startIndex, pager.endIndex + 1)
        this.setState({ pager: pager })

        this.props.onChangePage(pageOfItems)
    }

    getPager(totalItems, currentPage, pageSize) {
        currentPage = currentPage || 1;
        pageSize = pageSize || 10;

        let totalPages = Math.ceil(totalItems / pageSize);

        let startPage, endPage;
        if (totalPages <= 10) {
            startPage = 1;
            endPage = totalPages;
        } else {
            if (currentPage <= 6) {
                startPage = 1;
                endPage = 10;
            } else if (currentPage + 4 >= totalPages) {
                startPage = totalPages - 9;
                endPage = totalPages;
            } else {
                startPage = currentPage - 5;
                endPage = currentPage + 4;
            }
        }

        let startIndex = (currentPage - 1) * pageSize;
        let endIndex = Math.min(startIndex + pageSize - 1, totalItems - 1);

        let pages = [...Array((endPage + 1) - startPage).keys()].map(i => startPage + i);

        return {
            totalItems: totalItems,
            currentPage: currentPage,
            pageSize: pageSize,
            totalPages: totalPages,
            startPage: startPage,
            endPage: endPage,
            startIndex: startIndex,
            endIndex: endIndex,
            pages: pages
        };
    }

    render() {
        let pager = this.state.pager;

        if (!pager.pages || pager.pages.length <= 1) {
            return null;
        }

        return (
            <ul className="pagination">
                <li className={pager.currentPage === 1 ? 'page-item disabled' : 'page-item'}>
                    <Link className="page-link" to="/" onClick={e => this.setPage(e, 1)}>Primera</Link>
                </li>
                <li className={pager.currentPage === 1 ? 'page-item disabled' : 'page-item'}>
                    <Link className="page-link" to="/" onClick={e => this.setPage(e, pager.currentPage - 1)}>Anterior</Link>
                </li>
                {pager.pages.map((page, index) =>
                    <li key={index} className={pager.currentPage === page ? 'page-item active' : 'page-item'}>
                        <Link className="page-link" to="/" onClick={e => this.setPage(e, page)}>{page}</Link>
                    </li>
                )}
                <li className={pager.currentPage === pager.totalPages ? 'page-item disabled' : 'page-item'}>
                    <Link className="page-link" to="/" onClick={e => this.setPage(e, pager.currentPage + 1)}>Siguiente</Link>
                </li>
                <li className={pager.currentPage === pager.totalPages ? 'page-item disabled' : 'page-item'}>
                    <Link className="page-link" to="/" onClick={e => this.setPage(e, pager.totalPages)}>Última</Link>
                </li>
            </ul>
        );
    }
}

Pagination.defaultProps = defaultProps;
export default Pagination;