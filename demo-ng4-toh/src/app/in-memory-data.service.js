"use strict";
var InMemoryDataService = (function () {
    function InMemoryDataService() {
    }
    InMemoryDataService.prototype.createDb = function () {
        var heroes = [
            { id: 0, name: 'Zero' },
            { id: 1001, name: 'Mr. Nice' },
            { id: 1002, name: 'Narco' },
            { id: 1003, name: 'Bombasto' },
            { id: 1004, name: 'Celeritas' },
            { id: 1005, name: 'Magneta' },
            { id: 1006, name: 'RubberMan' },
            { id: 1007, name: 'Dynama' },
            { id: 1008, name: 'Dr IQ' },
            { id: 1009, name: 'Magma' },
            { id: 1010, name: 'Tornado' }
        ];
        return { heroes: heroes };
    };
    return InMemoryDataService;
}());
exports.InMemoryDataService = InMemoryDataService;
//# sourceMappingURL=in-memory-data.service.js.map