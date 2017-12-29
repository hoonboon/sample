import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

//used to create fake backend
import { authApiMockProvider } from './_mock/authApi.mock';

import { AppRoutingModule }  from './app-routing.module';

import { AppComponent } from './app.component';

// common
import { AlertComponent } from './common/alert/alert.component';
import { AlertService } from './common/services/alert.service';
import { LoggerService } from './common/services/logger.service';
import { ConsoleLoggerService } from './common/services/console-logger.service';
import { UserService } from './common/services/user.service';
import { AuthGuard } from './common/guards/auth.guard';

// others
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';

@NgModule({
  declarations: [
    AppComponent,
    AlertComponent,
    HomeComponent,
    LoginComponent,
    RegisterComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [ 
    AuthGuard, 
    AlertService,
    {provide: LoggerService, useClass: ConsoleLoggerService},
    UserService,
    // provider used to create fake backend
    authApiMockProvider
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
