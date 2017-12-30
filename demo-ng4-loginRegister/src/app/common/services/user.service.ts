import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { LoggerService } from './logger.service';
import { User } from '../models/user';

@Injectable()
export class UserService {
  
  static readonly baseUrl = '/api/users';
  
  constructor(
      private logger: LoggerService,
      private http: HttpClient) { }

  create(user: User) {
    this.logger.info('create() start');
    return this.http.post(UserService.baseUrl, user);
  }
  
  getAll() {
    return this.http.get<User[]>(UserService.baseUrl);
  }
  
  delete(id: number) {
    return this.http.delete(UserService.baseUrl + '/' + id)
  }
  
}