/*
 *   This content is licensed according to the W3C Software License at
 *   https://www.w3.org/Consortium/Legal/2015/copyright-software-and-document
 */

'use strict';

var aria = aria || {};

/**
 * @class
 * @description
 *  Combobox object representing the state and interactions for a combobox
 *  widget
 * @param input
 *  The input node
 * @param grid
 *  The grid node to load results in
 * @param searchFn
 *  The search function. The function accepts a search string and returns an
 *  array of results.
 */
aria.GridCombobox = function (input, grid, searchFn) {
    this.input = input;
    this.grid = grid;
    this.searchFn = searchFn;
    this.activeRowIndex = -1;
    this.activeColIndex = 0;
    this.rowsCount = 0;
    this.colsCount = 0;
    this.gridFocused = false;
    this.shown = false;
    this.selectionCol = 0;

    this.setupEvents();
};

aria.GridCombobox.prototype.setupEvents = function () {
    document.body.addEventListener('click', this.handleBodyClick.bind(this));
    this.input.addEventListener('keyup', this.handleInputKeyUp.bind(this));
    this.input.addEventListener('keydown', this.handleInputKeyDown.bind(this));
    this.input.addEventListener('focus', this.handleInputFocus.bind(this));
    this.grid.addEventListener('click', this.handleGridClick.bind(this));
};

aria.GridCombobox.prototype.handleBodyClick = function (evt) {
    if (evt.target === this.input || this.grid.contains(evt.target)) {
        return;
    }
    this.hideResults();
};

aria.GridCombobox.prototype.handleInputKeyUp = function (evt) {
    var key = evt.which || evt.keyCode;

    switch (key) {
        case aria.KeyCode.UP:
        case aria.KeyCode.DOWN:
        case aria.KeyCode.ESC:
        case aria.KeyCode.RETURN:
            evt.preventDefault();
            return;
        case aria.KeyCode.LEFT:
        case aria.KeyCode.RIGHT:
            if (this.gridFocused) {
                evt.preventDefault();
                return;
            }
            break;
        default:
            this.updateResults();
    }
};

aria.GridCombobox.prototype.handleInputKeyDown = function (evt) {
    var key = evt.which || evt.keyCode;
    var activeRowIndex = this.activeRowIndex;
    var activeColIndex = this.activeColIndex;

    if (key === aria.KeyCode.ESC) {
        if (this.gridFocused) {
            this.gridFocused = false;
            this.removeFocusCell(this.activeRowIndex, this.activeColIndex);
            this.activeRowIndex = -1;
            this.activeColIndex = 0;
            this.input.setAttribute('aria-activedescendant', '');
        } else {
            if (!this.shown) {
                setTimeout(
                    function () {
                        // On Firefox, input does not get cleared here unless wrapped in
                        // a setTimeout
                        this.input.value = '';
                    }.bind(this),
                    1
                );
            }
        }
        if (this.shown) {
            this.hideResults();
        }
        return;
    }

    if (this.rowsCount < 1) {
        return;
    }

    var prevActive = this.getItemAt(activeRowIndex, this.selectionCol);
    var activeItem;

    switch (key) {
        case aria.KeyCode.UP:
            this.gridFocused = true;
            activeRowIndex = this.getRowIndex(key);
            evt.preventDefault();
            break;
        case aria.KeyCode.DOWN:
            this.gridFocused = true;
            activeRowIndex = this.getRowIndex(key);
            evt.preventDefault();
            break;
        case aria.KeyCode.LEFT:
            if (activeColIndex <= 0) {
                activeColIndex = this.colsCount - 1;
                activeRowIndex = this.getRowIndex(key);
            } else {
                activeColIndex--;
            }
            if (this.gridFocused) {
                evt.preventDefault();
            }
            break;
        case aria.KeyCode.RIGHT:
            if (activeColIndex === -1 || activeColIndex >= this.colsCount - 1) {
                activeColIndex = 0;
                activeRowIndex = this.getRowIndex(key);
            } else {
                activeColIndex++;
            }
            if (this.gridFocused) {
                evt.preventDefault();
            }
            break;
        case aria.KeyCode.RETURN:
            activeItem = this.getItemAt(activeRowIndex, this.selectionCol);
            this.selectItem(activeItem);
            this.gridFocused = false;
            return;
        case aria.KeyCode.TAB:
            this.hideResults();
            return;
        default:
            return;
    }

    if (prevActive) {
        this.removeFocusCell(this.activeRowIndex, this.activeColIndex);
        prevActive.setAttribute('aria-selected', 'false');
    }

    activeItem = this.getItemAt(activeRowIndex, activeColIndex);
    this.activeRowIndex = activeRowIndex;
    this.activeColIndex = activeColIndex;

    if (activeItem) {
        this.input.setAttribute(
            'aria-activedescendant',
            'result-item-' + activeRowIndex + 'x' + activeColIndex
        );
        this.focusCell(activeRowIndex, activeColIndex);
        var selectedItem = this.getItemAt(activeRowIndex, this.selectionCol);
        selectedItem.setAttribute('aria-selected', 'true');
    } else {
        this.input.setAttribute('aria-activedescendant', '');
    }
};

aria.GridCombobox.prototype.handleInputFocus = function () {
    this.updateResults();
};

aria.GridCombobox.prototype.handleGridClick = function (evt) {
    if (!evt.target) {
        return;
    }

    var row;
    if (evt.target.getAttribute('role') === 'row') {
        row = evt.target;
    } else if (evt.target.getAttribute('role') === 'gridcell') {
        row = evt.target.parentNode;
    } else {
        return;
    }

    var selectItem = row.querySelector('.result-cell');
    this.selectItem(selectItem);
};

aria.GridCombobox.prototype.isElementInView = function (element) {
    var bounding = element.getBoundingClientRect();

    return (
        bounding.top >= 0 &&
        bounding.left >= 0 &&
        bounding.bottom <=
        (window.innerHeight || document.documentElement.clientHeight) &&
        bounding.right <=
        (window.innerWidth || document.documentElement.clientWidth)
    );
};

aria.GridCombobox.prototype.updateResults = function () {
    var searchString = this.input.value;
    var results = this.searchFn(searchString);

    this.hideResults();

    if (!searchString) {
        results = [];
    }

    if (results.length) {
        for (var row = 0; row < results.length; row++) {
            var resultRow = document.createElement('div');
            resultRow.className = 'result-row';
            resultRow.setAttribute('role', 'row');
            resultRow.setAttribute('id', 'result-row-' + row);
            for (var col = 0; col < results[row].length; col++) {
                var resultCell = document.createElement('div');
                resultCell.className = 'result-cell';
                resultCell.setAttribute('role', 'gridcell');
                resultCell.setAttribute('id', 'result-item-' + row + 'x' + col);
                resultCell.innerText = results[row][col];
                resultRow.appendChild(resultCell);
            }
            this.grid.appendChild(resultRow);
        }
        aria.Utils.removeClass(this.grid, 'hidden');
        this.input.setAttribute('aria-expanded', 'true');
        this.rowsCount = results.length;
        this.colsCount = results.length ? results[0].length : 0;
        this.shown = true;
    }
};

aria.GridCombobox.prototype.getRowIndex = function (key) {
    var activeRowIndex = this.activeRowIndex;

    switch (key) {
        case aria.KeyCode.UP:
        case aria.KeyCode.LEFT:
            if (activeRowIndex <= 0) {
                activeRowIndex = this.rowsCount - 1;
            } else {
                activeRowIndex--;
            }
            break;
        case aria.KeyCode.DOWN:
        case aria.KeyCode.RIGHT:
            if (activeRowIndex === -1 || activeRowIndex >= this.rowsCount - 1) {
                activeRowIndex = 0;
            } else {
                activeRowIndex++;
            }
    }

    return activeRowIndex;
};

aria.GridCombobox.prototype.getItemAt = function (rowIndex, colIndex) {
    return document.getElementById('result-item-' + rowIndex + 'x' + colIndex);
};

aria.GridCombobox.prototype.selectItem = function (item) {
    if (item) {
        this.input.value = item.innerText;
        this.hideResults();
    }
};

aria.GridCombobox.prototype.hideResults = function () {
    this.gridFocused = false;
    this.shown = false;
    this.activeRowIndex = -1;
    this.activeColIndex = 0;
    this.grid.innerHTML = '';
    aria.Utils.addClass(this.grid, 'hidden');
    this.input.setAttribute('aria-expanded', 'false');
    this.rowsCount = 0;
    this.colsCount = 0;
    this.input.setAttribute('aria-activedescendant', '');

    // ensure the input is in view
    if (!this.isElementInView(this.input)) {
        this.input.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    }
};

aria.GridCombobox.prototype.removeFocusCell = function (rowIndex, colIndex) {
    var row = document.getElementById('result-row-' + rowIndex);
    aria.Utils.removeClass(row, 'focused');
    var cell = this.getItemAt(rowIndex, colIndex);
    aria.Utils.removeClass(cell, 'focused-cell');
};

aria.GridCombobox.prototype.focusCell = function (rowIndex, colIndex) {
    var row = document.getElementById('result-row-' + rowIndex);
    aria.Utils.addClass(row, 'focused');
    var cell = this.getItemAt(rowIndex, colIndex);
    aria.Utils.addClass(cell, 'focused-cell');

    // ensure the cell is in view
    if (!this.isElementInView(cell)) {
        cell.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    }
};
/*
 *   This content is licensed according to the W3C Software License at
 *   https://www.w3.org/Consortium/Legal/2015/copyright-software-and-document
 *
 * ARIA Combobox Examples
 */

'use strict';

var aria = aria || {};

var COUNTRIES_AND_CITIES = [
    ['Chicago', 'USA'],
    ['New York', 'USA'],
    ['Los Angeles', 'USA'],
    ['Las Vegas', 'USA'],
    ['Boston', 'USA'],
    ['Miami', 'USA'],
    ['Barcelona', 'Spain'],
    ['Madrid', 'Spain'],
    ['Cabbage', 'Vegetable'],
    ['Carrot', 'Vegetable'],
    ['Cauliflower', 'Vegetable'],
    ['Celery', 'Vegetable'],
    ['Chard', 'Vegetable'],
    ['Chicory', 'Vegetable'],
    ['Corn', 'Vegetable'],
    ['Cucumber', 'Vegetable'],
    ['Daikon', 'Vegetable'],
    ['Date', 'Fruit'],
    ['Edamame', 'Vegetable'],
    ['Eggplant', 'Vegetable'],
    ['Elderberry', 'Fruit'],
    ['Fennel', 'Vegetable'],
    ['Fig', 'Fruit'],
    ['Garlic', 'Vegetable'],
    ['Grape', 'Fruit'],
    ['Honeydew melon', 'Fruit'],
    ['Iceberg lettuce', 'Vegetable'],
    ['Jerusalem artichoke', 'Vegetable'],
    ['Kale', 'Vegetable'],
    ['Kiwi', 'Fruit'],
    ['Leek', 'Vegetable'],
    ['Lemon', 'Fruit'],
    ['Mango', 'Fruit'],
    ['Mangosteen', 'Fruit'],
    ['Melon', 'Fruit'],
    ['Mushroom', 'Fungus'],
    ['Nectarine', 'Fruit'],
    ['Okra', 'Vegetable'],
    ['Olive', 'Vegetable'],
    ['Onion', 'Vegetable'],
    ['Orange', 'Fruit'],
    ['Parsnip', 'Vegetable'],
    ['Pea', 'Vegetable'],
    ['Pear', 'Fruit'],
    ['Pineapple', 'Fruit'],
    ['Potato', 'Vegetable'],
    ['Pumpkin', 'Fruit'],
    ['Quince', 'Fruit'],
    ['Radish', 'Vegetable'],
    ['Rhubarb', 'Vegetable'],
    ['Shallot', 'Vegetable'],
    ['Spinach', 'Vegetable'],
    ['Squash', 'Vegetable'],
    ['Strawberry', 'Fruit'],
    ['Sweet potato', 'Vegetable'],
    ['Tomato', 'Fruit'],
    ['Turnip', 'Vegetable'],
    ['Ugli fruit', 'Fruit'],
    ['Victoria plum', 'Fruit'],
    ['Watercress', 'Vegetable'],
    ['Watermelon', 'Fruit'],
    ['Yam', 'Vegetable'],
    ['Zucchini', 'Vegetable'],
];

function searchVeggies(searchString) {
    var results = [];

    for (var i = 0; i < COUNTRIES_AND_CITIES.length; i++) {
        var veggie = COUNTRIES_AND_CITIES[i][0].toLowerCase();
        if (veggie.indexOf(searchString.toLowerCase()) === 0) {
            results.push(COUNTRIES_AND_CITIES[i]);
        }
    }

    return results;
}

/**
 * @function onload
 * @description Initialize the combobox examples once the page has loaded
 */
window.addEventListener('load', function () {
    new aria.GridCombobox(
        document.getElementById('ex1-input'),
        document.getElementById('ex1-grid'),
        searchVeggies
    );
});
'use strict';
/**
 * @namespace aria
 */

var aria = aria || {};

/**
 * @description
 *  Key code constants
 */
aria.KeyCode = {
    BACKSPACE: 8,
    TAB: 9,
    RETURN: 13,
    SHIFT: 16,
    ESC: 27,
    SPACE: 32,
    PAGE_UP: 33,
    PAGE_DOWN: 34,
    END: 35,
    HOME: 36,
    LEFT: 37,
    UP: 38,
    RIGHT: 39,
    DOWN: 40,
    DELETE: 46,
};

aria.Utils = aria.Utils || {};

// Polyfill src https://developer.mozilla.org/en-US/docs/Web/API/Element/matches
aria.Utils.matches = function (element, selector) {
    if (!Element.prototype.matches) {
        Element.prototype.matches =
            Element.prototype.matchesSelector ||
            Element.prototype.mozMatchesSelector ||
            Element.prototype.msMatchesSelector ||
            Element.prototype.oMatchesSelector ||
            Element.prototype.webkitMatchesSelector ||
            function (s) {
                var matches = element.parentNode.querySelectorAll(s);
                var i = matches.length;
                while (--i >= 0 && matches.item(i) !== this) {
                    // empty
                }
                return i > -1;
            };
    }

    return element.matches(selector);
};

aria.Utils.remove = function (item) {
    if (item.remove && typeof item.remove === 'function') {
        return item.remove();
    }
    if (
        item.parentNode &&
        item.parentNode.removeChild &&
        typeof item.parentNode.removeChild === 'function'
    ) {
        return item.parentNode.removeChild(item);
    }
    return false;
};

aria.Utils.isFocusable = function (element) {
    if (element.tabIndex < 0) {
        return false;
    }

    if (element.disabled) {
        return false;
    }

    switch (element.nodeName) {
        case 'A':
            return !!element.href && element.rel != 'ignore';
        case 'INPUT':
            return element.type != 'hidden';
        case 'BUTTON':
        case 'SELECT':
        case 'TEXTAREA':
            return true;
        default:
            return false;
    }
};

aria.Utils.getAncestorBySelector = function (element, selector) {
    if (!aria.Utils.matches(element, selector + ' ' + element.tagName)) {
        // Element is not inside an element that matches selector
        return null;
    }

    // Move up the DOM tree until a parent matching the selector is found
    var currentNode = element;
    var ancestor = null;
    while (ancestor === null) {
        if (aria.Utils.matches(currentNode.parentNode, selector)) {
            ancestor = currentNode.parentNode;
        } else {
            currentNode = currentNode.parentNode;
        }
    }

    return ancestor;
};

aria.Utils.hasClass = function (element, className) {
    return new RegExp('(\\s|^)' + className + '(\\s|$)').test(element.className);
};

aria.Utils.addClass = function (element, className) {
    if (!aria.Utils.hasClass(element, className)) {
        element.className += ' ' + className;
    }
};

aria.Utils.removeClass = function (element, className) {
    var classRegex = new RegExp('(\\s|^)' + className + '(\\s|$)');
    element.className = element.className.replace(classRegex, ' ').trim();
};

aria.Utils.bindMethods = function (object /* , ...methodNames */) {
    var methodNames = Array.prototype.slice.call(arguments, 1);
    methodNames.forEach(function (method) {
        object[method] = object[method].bind(object);
    });
};
