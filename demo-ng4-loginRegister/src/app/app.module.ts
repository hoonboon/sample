import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule }  from './app-routing.module';

import { AppComponent } from './app.component';

// common
import { AlertComponent } from './common/alert/alert.component';
import { AlertService } from './common/services/alert.service';
import { AuthGuard } from './common/guards/auth.guard';

// others
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';

@NgModule({
  declarations: [
    AppComponent,
    AlertComponent,
    HomeComponent,
    LoginComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [ 
    AuthGuard, 
    AlertService 
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
