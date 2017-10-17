import { InMemoryDbService } from 'angular-in-memory-web-api';

export class InMemoryDataService implements InMemoryDbService {
  createDb() {
    const heroes = [
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
  
    return {heroes};
  }
}